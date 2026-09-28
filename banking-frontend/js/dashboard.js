function loadUserInfo() {

    const token = getAccessToken();

    if (!token) {
        return;
    }

    try {

        const payload =
            JSON.parse(atob(token.split(".")[1]));

        const username =
            payload.preferred_username || "User";

        const roles =
            payload.realm_access?.roles || [];

        document.getElementById("username").innerText =
            username;

        let role = "User";

        if (roles.includes("admin")) {
            role = "ADMIN";
        } else if (roles.includes("bank-staff")) {
            role = "BANK STAFF";
        } else if (roles.includes("customer")) {
            role = "CUSTOMER";
        }

        document.getElementById("userRole").innerText =
            role;

    } catch (error) {

        console.error("Unable to read user information");

    }
}
async function loadDashboard() {

    try {

        // Get customers
        const customerResponse =
            await apiFetch(`${API_BASE_URL}/customers`);

        const customers =
            await customerResponse.json();

        document.getElementById("customerCount").innerText =
            customers.length;


        // Get accounts
        const accountResponse =
            await apiFetch(`${API_BASE_URL}/accounts`);

        const accounts =
            await accountResponse.json();

        document.getElementById("accountCount").innerText =
            accounts.length;


        // Get beneficiaries
        const beneficiaryResponse =
            await apiFetch(`${API_BASE_URL}/beneficiaries`);

        const beneficiaries =
            await beneficiaryResponse.json();

        document.getElementById("beneficiaryCount").innerText =
            beneficiaries.length;

    } catch (error) {

        console.error(error);

        alert("Unable to load dashboard data");
    }
}
loadUserInfo();
loadDashboard();