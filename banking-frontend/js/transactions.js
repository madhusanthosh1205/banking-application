async function loadAccountBalance() {

    const accountId =
        document.getElementById("accountId").value;

    const balanceElement =
        document.getElementById("accountBalance");


    if (!accountId) {

        balanceElement.innerText = "";

        return;
    }


    try {

        const response = await apiFetch(
            `${API_BASE_URL}/accounts/${accountId}`
        );


        if (!response.ok) {

            balanceElement.innerText =
                "Account not found";

            return;
        }


        const account = await response.json();


        balanceElement.innerText =
            `Current Balance: ₹${account.balance}`;


    } catch (error) {

        console.error(error);

        balanceElement.innerText =
            "Unable to load balance";

    }

}



async function performTransaction() {

    const accountId =
        document.getElementById("accountId").value;


    const type =
        document.getElementById("transactionType").value;


    const amount =
        Number(
            document.getElementById("amount").value
        );


    const description =
        document.getElementById("description").value;


    const transaction = {

        type: type,

        amount: amount,

        description: description

    };


    console.log(
        "Sending transaction:",
        transaction
    );


    try {

        const response = await apiFetch(

            `${API_BASE_URL}/accounts/${accountId}/transactions`,

            {

                method: "POST",

                headers: {

                    "Content-Type":
                        "application/json"

                },

                body:
                    JSON.stringify(transaction)

            }

        );


        const data =
            await response.json();


        console.log(
            "Backend response:",
            data
        );


        if (!response.ok) {

            alert(
                data.message ||
                JSON.stringify(data)
            );

            return;
        }


        alert(
            `${type} successful!`
        );


        // Get the latest balance
        loadAccountBalance();


        // Refresh transaction history
        loadTransactions();
        if (!hasRole("customer")) {

    document.getElementById("transactionControls")
        .style.display = "none";

    document.getElementById("transferControls")
        .style.display = "none";
}

    }


    catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );

    }

}



async function transferMoney() {

    const senderAccountId =
        document.getElementById(
            "senderAccountId"
        ).value;


    const receiverAccountId =
        Number(
            document.getElementById(
                "receiverAccountId"
            ).value
        );


    const amount =
        Number(
            document.getElementById(
                "transferAmount"
            ).value
        );


    const description =
        document.getElementById(
            "transferDescription"
        ).value;


    const transfer = {

        receiverAccountId:
            receiverAccountId,

        amount:
            amount,

        description:
            description

    };


    try {

        const response = await apiFetch(

            `${API_BASE_URL}/accounts/${senderAccountId}/transactions/transfer`,

            {

                method: "POST",

                headers: {

                    "Content-Type":
                        "application/json"

                },

                body:
                    JSON.stringify(transfer)

            }

        );


        const data =
            await response.json();


        if (!response.ok) {

            alert(
                data.message ||
                JSON.stringify(data)
            );

            return;
        }


        alert(
            "Transfer successful!"
        );


        // Refresh sender balance
        document.getElementById("accountId").value =
            senderAccountId;

        loadAccountBalance();


        loadTransactions();


    }


    catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );

    }

}



async function loadTransactions() {

    const accountId =
        document.getElementById(
            "accountId"
        ).value;


    if (!accountId) {

        alert(
            "Enter Account ID first"
        );

        return;

    }


    try {

        const response = await apiFetch(

            `${API_BASE_URL}/accounts/${accountId}/transactions`

        );


        const transactions =
            await response.json();


        const tableBody =
            document.getElementById(
                "transactionTableBody"
            );


        tableBody.innerHTML = "";


        transactions.forEach(transaction => {

            const row =
                document.createElement("tr");


            row.innerHTML = `

                <td>
                    ${transaction.id}
                </td>

                <td>
                    ${transaction.transactionReference}
                </td>

                <td>
                    ${transaction.transactionType}
                </td>

                <td>
                    ₹${transaction.amount}
                </td>

                <td>
                    ${transaction.transactionDate}
                </td>

                <td>
                    ${transaction.description || ""}
                </td>

            `;


            tableBody.appendChild(row);

        });


    }


    catch (error) {

        console.error(error);

        alert(
            "Unable to load transactions"
        );

    }

}