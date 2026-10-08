async function loadAccounts() {

    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts`
        );

        if (!response.ok) {

            const data = await response.json();

            alert(
                data.message ||
                "Unable to load accounts"
            );

            return;
        }

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

            }

            

            else if (hasRole("maker")) {

                row.innerHTML = `
                    <td>${account.id}</td>
                    <td>${account.accountNumber}</td>
                    <td>${account.accountType}</td>
                    <td>₹ ${account.balance}</td>
                    <td>${account.customerId}</td>
                    <td>${account.customerName}</td>
                    
                `;

            }

            /*
             * CUSTOMER
             * Backend already returns only their own accounts.
             */

            else if (hasRole("customer")) {

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
            Number(
                document.getElementById("customerId").value
            ),

        accountType:
            document.getElementById("accountType").value,

        initialBalance:
            Number(
                document.getElementById("initialBalance").value
            )

    };


    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts`,
            {
                method: "POST",

                body: account
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
        confirm(
            "Are you sure you want to delete this account?"
        );

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


/*
 * ROLE-BASED UI
 */

if (!hasRole("admin")) {

    const addAccountButton =
        document.getElementById("addAccountButton");

    const accountActionHeader =
        document.getElementById("accountActionHeader");


    if (addAccountButton) {

        addAccountButton.style.display = "none";

    }

    if (accountActionHeader) {

        accountActionHeader.style.display = "none";

    }

}


loadAccounts();