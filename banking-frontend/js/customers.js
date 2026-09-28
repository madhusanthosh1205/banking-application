async function loadCustomers() {

    try {

        const response =
            await apiFetch(`${API_BASE_URL}/customers`);

        const customers = await response.json();

        const tableBody =
            document.getElementById("customerTableBody");

        tableBody.innerHTML = "";

        customers.forEach(customer => {

            const row = document.createElement("tr");

            if (hasRole("admin")) {

    row.innerHTML = `
        <td>${customer.id}</td>
        <td>${customer.name}</td>
        <td>${customer.email}</td>
        <td>${customer.phone}</td>
        <td>
            <button onclick="deleteCustomer(${customer.id})">
                Delete
            </button>
        </td>
    `;

} else {

    row.innerHTML = `
        <td>${customer.id}</td>
        <td>${customer.name}</td>
        <td>${customer.email}</td>
        <td>${customer.phone}</td>
    `;
}

            tableBody.appendChild(row);
        });

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


async function createCustomer() {

    const customer = {

        name: document.getElementById("name").value,

        email: document.getElementById("email").value,

        phone: document.getElementById("phone").value
    };


    // Basic validation

    if (!customer.name ||
        !customer.email ||
        !customer.phone) {

        alert("Please fill all fields");

        return;
    }


    try {

        const response =
            await apiFetch(
                `${API_BASE_URL}/customers`,
                {
                    method: "POST",
                    body: customer
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


        alert("Customer created successfully!");


        // Clear fields

        document.getElementById("name").value = "";

        document.getElementById("email").value = "";

        document.getElementById("phone").value = "";


        // Hide form

        document.getElementById("customerForm")
            .style.display = "none";


        // Refresh customer list

        loadCustomers();

    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");
    }
}
async function deleteCustomer(id) {

    const confirmDelete =
        confirm("Are you sure you want to delete this customer?");

    if (!confirmDelete) {
        return;
    }

    try {

        const response = await apiFetch(
            `${API_BASE_URL}/customers/${id}`,
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

        alert("Customer deleted successfully!");

        loadCustomers();

    } catch (error) {

        console.error(error);

        alert("Unable to connect to backend");
    }
}

if (!hasRole("admin")) {

    document.getElementById("addCustomerButton")
        .style.display = "none";

    document.getElementById("actionHeader")
        .style.display = "none";
}

// Load customers when page opens

loadCustomers();