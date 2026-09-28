async function loadCustomers() {

    try {

        const response =
            await fetch(`${API_BASE_URL}/customers`);

        const customers =
            await response.json();

        const tableBody =
            document.getElementById("customerTableBody");

        tableBody.innerHTML = "";

        customers.forEach(customer => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${customer.id}</td>
                <td>${customer.name}</td>
                <td>${customer.email}</td>
                <td>${customer.phone}</td>
            `;

            tableBody.appendChild(row);

        });

    } catch (error) {

        console.error("Error:", error);

    }
}
async function createCustomer() {

    const customer = {

        name: document.getElementById("name").value,

        email: document.getElementById("email").value,

        phone: document.getElementById("phone").value
    };

    try {

        const response =
            await fetch(`${API_BASE_URL}/customers`, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(customer)
            });

        const data = await response.json();

        if (!response.ok) {

            alert(
                data.message ||
                JSON.stringify(data)
            );

            return;
        }

        alert("Customer created successfully!");

        document.getElementById("name").value = "";
        document.getElementById("email").value = "";
        document.getElementById("phone").value = "";

        loadCustomers();

    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");

    }
}
function showCustomerForm() {

    const form =
        document.getElementById("customerForm");

    if (form.style.display === "none") {

        form.style.display = "block";

    } else {

        form.style.display = "none";

    }
}
loadCustomers();