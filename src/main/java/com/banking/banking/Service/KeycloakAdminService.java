package com.banking.banking.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final WebClient webClient;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    public KeycloakAdminService() {
        this.webClient = WebClient.builder().build();
    }

    /*
     * Get an access token using the banking-admin client.
     */
    private String getAdminAccessToken() {

        String tokenUrl =
                keycloakServerUrl
                        + "/realms/"
                        + realm
                        + "/protocol/openid-connect/token";

        Map<String, String> response =
                webClient.post()
                        .uri(tokenUrl)
                        .contentType(
                                MediaType.APPLICATION_FORM_URLENCODED)
                        .body(
                                BodyInserters
                                        .fromFormData(
                                                "client_id",
                                                adminClientId)
                                        .with(
                                                "client_secret",
                                                adminClientSecret)
                                        .with(
                                                "grant_type",
                                                "client_credentials")
                        )
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

        if (response == null ||
                response.get("access_token") == null) {

            throw new RuntimeException(
                    "Unable to obtain Keycloak admin access token");
        }

        return response
                .get("access_token")
                .toString();
    }
    public void deleteUser(String userId) {

        String accessToken = getAdminAccessToken();

        String deleteUserUrl =
                keycloakServerUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId;

        webClient.delete()
                .uri(deleteUserUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .toBodilessEntity()
                .block();
    }
    /*
     * Create a new Keycloak customer user.
     */
    public String createCustomerUser(
            String username,
            String email,
            String password) {

        String accessToken = getAdminAccessToken();

        String createUserUrl =
                keycloakServerUrl
                        + "/admin/realms/"
                        + realm
                        + "/users";

        Map<String, Object> user = new HashMap<>();

        user.put("username", username);
        user.put("email", email);
        user.put("enabled", true);
        user.put("emailVerified", false);

        Map<String, Object> credentials =
                new HashMap<>();

        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);

        user.put(
                "credentials",
                List.of(credentials)
        );

        URI location =
                webClient.post()
                        .uri(createUserUrl)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user)
                        .retrieve()
                        .toBodilessEntity()
                        .block()
                        .getHeaders()
                        .getLocation();

        if (location == null) {
            throw new RuntimeException(
                    "Keycloak user was not created");
        }

        String userId =
                extractUserIdFromLocation(location);

        assignCustomerRole(
                userId,
                accessToken
        );

        return userId;
    }

    /*
     * Assign the customer realm role to the newly
     * created Keycloak user.
     */
    private void assignCustomerRole(
            String userId,
            String accessToken) {

        String roleUrl =
                keycloakServerUrl
                        + "/admin/realms/"
                        + realm
                        + "/roles/customer";

        Map<String, Object> customerRole =
                webClient.get()
                        .uri(roleUrl)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

        if (customerRole == null ||
                customerRole.get("id") == null) {

            throw new RuntimeException(
                    "Customer role not found in Keycloak");
        }

        String assignRoleUrl =
                keycloakServerUrl
                        + "/admin/realms/"
                        + realm
                        + "/users/"
                        + userId
                        + "/role-mappings/realm";

        webClient.post()
                .uri(assignRoleUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(customerRole))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    /*
     * Keycloak returns the newly created user's ID
     * inside the Location header.
     */
    private String extractUserIdFromLocation(
            URI location) {

        String path =
                location.getPath();

        int lastSlash =
                path.lastIndexOf('/');

        if (lastSlash == -1 ||
                lastSlash == path.length() - 1) {

            throw new RuntimeException(
                    "Unable to extract Keycloak user ID");
        }

        return path.substring(lastSlash + 1);
    }
}