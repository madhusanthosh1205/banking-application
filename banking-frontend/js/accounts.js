async function loadAccounts() {

    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts`
        );

        const accounts = await response.json();

        const tableBody =
            document.getElementById("accountTableBody");

        tableBody.innerHTML = "";


        accounts.forEach(account => {

            const row = document.createElement("tr");

            if (hasRole("admin")) {

    row.innerHTML = `
        <td>${account.id}</td>
        <td>${account.accountNumber}</td>
        <td>${account.accountType}</td>
        <td>₹ ${account.balance}</td>
        <td>${account.customerId}</td>
        <td>${account.customerName}</td>
        <td>
            <button onclick="deleteAccount(${account.id})">
                Delete
            </button>
        </td>
    `;

} else {

    row.innerHTML = `
        <td>${account.id}</td>
        <td>${account.accountNumber}</td>
        <td>${account.accountType}</td>
        <td>₹ ${account.balance}</td>
        <td>${account.customerId}</td>
        <td>${account.customerName}</td>
    `;
}
            tableBody.appendChild(row);

        });

    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");

    }
}



function showAccountForm() {

    const form =
        document.getElementById("accountForm");

    if (form.style.display === "none") {

        form.style.display = "block";

    } else {

        form.style.display = "none";

    }

}



async function createAccount() {

    const account = {

        customerId:
            Number(document.getElementById("customerId").value),

        accountType:
            document.getElementById("accountType").value,

        initialBalance:
            Number(document.getElementById("initialBalance").value)

    };


    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(account)
            }
        );


        const data = await response.json();


        if (!response.ok) {

            alert(
                data.message ||
                JSON.stringify(data)
            );

            return;
        }


        alert("Account created successfully!");


        document.getElementById("customerId").value = "";

        document.getElementById("initialBalance").value = "";


        loadAccounts();


    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");

    }

}

async function deleteAccount(id) {

    const confirmDelete =
        confirm("Are you sure you want to delete this account?");

    if (!confirmDelete) {
        return;
    }

    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts/${id}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {

            const data = await response.json();

            alert(
                data.message ||
                JSON.stringify(data)
            );

            return;
        }

        alert("Account deleted successfully!");

        loadAccounts();

    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");

    }
}
if (!hasRole("admin")) {

    document.getElementById("addAccountButton")
        .style.display = "none";

    document.getElementById("accountActionHeader")
        .style.display = "none";
}
loadAccounts();