async function loadBeneficiaries() {

    try {

        const response = await apiFetch(
            `${API_BASE_URL}/beneficiaries`
        );


        const beneficiaries =
            await response.json();


        const tableBody =
            document.getElementById(
                "beneficiaryTableBody"
            );


        tableBody.innerHTML = "";


        beneficiaries.forEach(beneficiary => {

            const row =
                document.createElement("tr");


            row.innerHTML = `

                <td>
                    ${beneficiary.id}
                </td>

                <td>
                    ${beneficiary.name}
                </td>

                <td>
                    ${beneficiary.accountNumber}
                </td>

                <td>
                    ${beneficiary.bankName}
                </td>

                <td>
                    ${beneficiary.customerId}
                </td>

                <td>
                    ${beneficiary.createdAt}
                </td>

             <td>

    ${
        hasRole("admin") || hasRole("bank-staff")
        ? `
            <button
                onclick="deleteBeneficiary(${beneficiary.id})"
            >
                Delete
            </button>
          `
        : ""
    }

</td>

            `;


            tableBody.appendChild(row);

        });


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );

    }

}



function showBeneficiaryForm() {

    const form =
        document.getElementById(
            "beneficiaryForm"
        );


    if (form.style.display === "none") {

        form.style.display = "block";

    } else {

        form.style.display = "none";

    }

}



async function createBeneficiary() {

    const beneficiary = {

        customerId:
            Number(
                document.getElementById(
                    "customerId"
                ).value
            ),

        name:
            document.getElementById(
                "beneficiaryName"
            ).value,

        accountNumber:
            document.getElementById(
                "accountNumber"
            ).value,

        bankName:
            document.getElementById(
                "bankName"
            ).value

    };


    try {

        const response = await apiFetch(

            `${API_BASE_URL}/beneficiaries`,

            {

                method: "POST",

                headers: {

                    "Content-Type":
                        "application/json"

                },

                body:
                    JSON.stringify(beneficiary)

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
            "Beneficiary created successfully!"
        );


        loadBeneficiaries();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );

    }

}



async function deleteBeneficiary(id) {

    const confirmDelete =
        confirm(
            "Are you sure you want to delete this beneficiary?"
        );


    if (!confirmDelete) {

        return;

    }


    try {

        const response = await apiFetch(

            `${API_BASE_URL}/beneficiaries/${id}`,

            {

                method: "DELETE"

            }

        );


        if (!response.ok) {

            const data =
                await response.json();

            alert(
                data.message ||
                "Delete failed"
            );

            return;

        }


        alert(
            "Beneficiary deleted successfully!"
        );


        loadBeneficiaries();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );

    }

}



loadBeneficiaries();