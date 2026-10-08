
const API_BASE_URL = "/api";

let accessToken = sessionStorage.getItem("accessToken");//keycloack login

//function needs for login.Js
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
            JSON.parse(atob(token.split(".")[1]));//decode the payload part[1] and convert to js object

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
function setupRoleBasedNavigation() {

    const roles = getUserRoles();

    const isCustomer = roles.includes("customer");
    const isMaker = roles.includes("maker");
    const isChecker = roles.includes("checker");
    const isAdmin = roles.includes("admin");

    const navLinks =
        document.querySelectorAll(".navbar a");

    navLinks.forEach(link => {

        const href =
            link.getAttribute("href");

        // ADMIN
        // Admin sees everything
        if (isAdmin) {
            return;
        }

        // CHECKER
        // Checker sees only Consents
        if (isChecker) {

            if (href !== "consents.html") {
                link.style.display = "none";
            }

            return;
        }

        // MAKER
        // Maker does not need Beneficiaries or Consents
        if (isMaker) {

            if (
                href === "beneficiaries.html" ||
                href === "consents.html"
            ) {
                link.style.display = "none";
            }

            return;
        }

        // CUSTOMER
        // Customer does not need Customers page
        if (isCustomer) {

            if (href === "customers.html") {
                link.style.display = "none";
            }

            return;
        }

    });
}


document.addEventListener(
    "DOMContentLoaded",
    function () {

        setupRoleBasedNavigation();

    }
);