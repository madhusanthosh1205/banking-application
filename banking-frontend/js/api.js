
const API_BASE_URL = "/api";

let accessToken = sessionStorage.getItem("accessToken");

function setAccessToken(token) {
    accessToken = token;
    sessionStorage.setItem("accessToken", token);
}

function getAccessToken() {
    return accessToken;
}

async function apiFetch(url, options = {}) {

    const headers = {
        ...(options.headers || {})
    };


    if (accessToken) {
        headers["Authorization"] = `Bearer ${accessToken}`;
    }

    if (options.body && typeof options.body !== "string") {
        headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(options.body);
    }


    return fetch(url, {
        ...options,
        headers
    });
}

    function getUserRoles() {

    const token = getAccessToken();

    if (!token) {
        return [];
    }

    try {

        const payload =
            JSON.parse(atob(token.split(".")[1]));

        return payload.realm_access?.roles || [];

    } catch (error) {

        console.error("Unable to read user roles");

        return [];
    }
}


function hasRole(role) {

    return getUserRoles().includes(role);
}

function logout() {
    sessionStorage.removeItem("accessToken");
    window.location.href = "login.html";
}

function requireLogin() {
    if (!getAccessToken()) {
        window.location.href = "login.html";
    }
}