package com.banking.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth

                        // Account APIs - View
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/accounts",
                                "/api/accounts/**"
                        ).authenticated()

                        // Create account - Admin only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/accounts"
                        ).hasRole("admin")

                        // Delete account - Admin only
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/accounts/**"
                        ).hasRole("admin")

                        // Add customer - Admin only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/customers"
                        ).hasRole("admin")

                        // Transactions - Customer only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/accounts/*/transactions",
                                "/api/accounts/*/transactions/transfer"
                        ).hasRole("customer")

                        // Delete beneficiary - Admin or Bank Staff
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/beneficiaries/**"
                        ).hasAnyRole("admin", "bank-staff")

                        // Other APIs - authenticated users
                        .requestMatchers("/api/**")
                        .authenticated()

                        .anyRequest()
                        .permitAll()
                )
                .oauth2ResourceServer(
                        oauth2 -> oauth2
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                ))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(this::extractRoles);

        return converter;
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        if (realmAccess != null) {

            Object rolesObject = realmAccess.get("roles");

            if (rolesObject instanceof Collection<?> roles) {

                for (Object role : roles) {

                    authorities.add(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role.toString()
                            )
                    );
                }
            }
        }

        return authorities;
    }
}