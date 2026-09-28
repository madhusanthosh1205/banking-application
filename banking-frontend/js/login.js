async function login() {

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    const formData = new URLSearchParams();

    formData.append("client_id", "banking");
    formData.append("grant_type", "password");
    formData.append("username", username);
    formData.append("password", password);

    try {

        const response = await fetch(
            "http://localhost:8180/realms/banking-realm/protocol/openid-connect/token",
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: formData
            }
        );

        const data = await response.json();

        if (!response.ok) {
            document.getElementById("message").innerText =
                data.error_description || "Login failed";

            return;
        }

        setAccessToken(data.access_token);
console.log("JWT saved:", data.access_token);

        document.getElementById("message").innerText =
            "Login successful!";
window.location.href = "accounts.html";
        console.log("JWT received");

    } catch (error) {

        console.error(error);

        document.getElementById("message").innerText =
            "Unable to connect to Keycloak";
    }
}