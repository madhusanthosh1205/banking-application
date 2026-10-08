// ========================================
// LOAD ALL CONSENTS
// ========================================

async function loadConsents() {

    try {

        const response =
            await apiFetch(
                `${API_BASE_URL}/consents`
            );


        if (!response.ok) {

            const data =
                await response.json();

            alert(
                data.message ||
                "Unable to load consents"
            );

            return;
        }


        const consents =
            await response.json();


        const tableBody =
            document.getElementById(
                "consentTableBody"
            );


        tableBody.innerHTML = "";


        consents.forEach(consent => {

            const row =
                document.createElement("tr");


            // ========================================
            // CHECKER ACTION BUTTONS
            // ========================================

            let actionHTML = "";


            if (
                hasRole("checker") &&
                consent.status === "PENDING"
            ) {

                actionHTML = `
                    <button
                        onclick="approveConsent(${consent.id})">

                        Approve

                    </button>

                    <button
                        onclick="rejectConsent(${consent.id})">

                        Reject

                    </button>
                `;

            } else {

                actionHTML = "-";

            }


            // ========================================
            // TABLE ROW
            // ========================================

            row.innerHTML = `

                <td>
                    ${consent.id}
                </td>

                <td>
                    ${consent.customerName}
                    (ID: ${consent.customerId})
                </td>

                <td>
                    ${consent.accountNumber}
                    (ID: ${consent.accountId})
                </td>

                <td>
                    ${consent.purpose}
                </td>

                <td>
                    ${consent.status}
                </td>

                <td>
                    ${formatDate(consent.createdAt)}
                </td>

                <td>
                    ${consent.reviewedBy || "-"}
                </td>

                <td>
                    ${actionHTML}
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



// ========================================
// SHOW / HIDE CONSENT FORM
// ========================================

function showConsentForm() {

    const form =
        document.getElementById(
            "consentForm"
        );


    if (form.style.display === "none") {

        form.style.display = "block";

    } else {

        form.style.display = "none";

    }
}



// ========================================
// CREATE CONSENT
// ========================================

async function createConsent() {

    const customerId =
        document.getElementById(
            "customerId"
        ).value;


    const accountId =
        document.getElementById(
            "accountId"
        ).value;


    const purpose =
        document.getElementById(
            "purpose"
        ).value.trim();


    // ========================================
    // BASIC VALIDATION
    // ========================================

    if (
        !customerId ||
        !accountId ||
        !purpose
    ) {

        alert(
            "Please fill all fields"
        );

        return;
    }


    const consent = {

        customerId:
            Number(customerId),

        accountId:
            Number(accountId),

        purpose:
            purpose

    };


    try {

        const response =
            await apiFetch(
                `${API_BASE_URL}/consents`,
                {
                    method: "POST",

                    body: consent
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
            "Consent created successfully!"
        );


        // ========================================
        // CLEAR FORM
        // ========================================

        document.getElementById(
            "customerId"
        ).value = "";


        document.getElementById(
            "accountId"
        ).value = "";


        document.getElementById(
            "purpose"
        ).value = "";


        document.getElementById(
            "consentForm"
        ).style.display = "none";


        // ========================================
        // REFRESH TABLE
        // ========================================

        loadConsents();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );
    }
}



// ========================================
// APPROVE CONSENT
// ========================================

async function approveConsent(id) {

    const confirmed =
        confirm(
            "Are you sure you want to approve this consent?"
        );


    if (!confirmed) {

        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE_URL}/consents/${id}/approve`,
                {
                    method: "PUT"
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
            "Consent approved successfully!"
        );


        loadConsents();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );
    }
}



// ========================================
// REJECT CONSENT
// ========================================

async function rejectConsent(id) {

    const confirmed =
        confirm(
            "Are you sure you want to reject this consent?"
        );


    if (!confirmed) {

        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE_URL}/consents/${id}/reject`,
                {
                    method: "PUT"
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
            "Consent rejected successfully!"
        );


        loadConsents();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend"
        );
    }
}



// ========================================
// FORMAT DATE
// ========================================

function formatDate(dateString) {

    if (!dateString) {

        return "-";
    }


    const date =
        new Date(dateString);


    return date.toLocaleString();
}



// ========================================
// ROLE-BASED UI
// ========================================

function configureConsentUI() {

    const addButton =
        document.getElementById(
            "addConsentButton"
        );


    const actionHeader =
        document.getElementById(
            "actionHeader"
        );


    // Customer and Maker can create consent

    if (
        !hasRole("customer") &&
        !hasRole("maker")
    ) {

        addButton.style.display =
            "none";
    }


    // Checker needs Action column

    if (!hasRole("checker")) {

        actionHeader.style.display =
            "none";
    }
}



// ========================================
// PAGE INITIALIZATION
// ========================================

configureConsentUI();

loadConsents();