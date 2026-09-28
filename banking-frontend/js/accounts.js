async function loadAccounts() {

    try {

        const response = await fetch(
            `${API_BASE_URL}/accounts`
        );

        const accounts = await response.json();

        const tableBody =
            document.getElementById("accountTableBody");

        tableBody.innerHTML = "";


        accounts.forEach(account => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${account.id}</td>
                <td>${account.accountNumber}</td>
                <td>${account.accountType}</td>
                <td>₹ ${account.balance}</td>
                <td>${account.customerId}</td>
                <td>${account.customerName}</td>
            `;

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

        const response = await fetch(
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


loadAccounts();