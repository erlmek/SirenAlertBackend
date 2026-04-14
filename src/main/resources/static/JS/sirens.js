import {fetchAnyUrl, postObjectAsJson} from "./module.js";

const urlSirens = "http://localhost:8080/sirens/all"
const sirenTable = document.getElementById("SirenTableBody");
const createSirenButton = document.getElementById("sirenKnap");
const createModal = document.getElementById("createModal");
const updateModal = document.getElementById("updateModal");
const closeBtn = document.querySelector(".close");

function insertSirens(siren, index) {
    console.log("Siren object:", siren);
    const tr = document.createElement("tr")
    tr.innerHTML =
        "<td>" + siren.id + "</td>" +
        "<td>" + siren.name + "</td>" +
        "<td>" + siren.latitude.toFixed(2) + "</td>" +
        "<td>" + siren.longitude.toFixed(2) + "</td>" +
        "<td>" + siren.status + "</td>" +
        "<td>" + (siren.disabled ? "Yes" : "No") + "</td>"

    // Add action buttons cell
    const actionCell = document.createElement("td");

    // Update button
    const updateButton = document.createElement("button");
    updateButton.textContent = "Rediger";
    updateButton.onclick = () => openUpdateModal(siren);

    // Delete button
    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Slet";
    deleteButton.onclick = () => deleteSiren(siren.id);

    actionCell.appendChild(updateButton);
    actionCell.appendChild(deleteButton);
    tr.appendChild(actionCell);

    tr.row = index
    sirenTable.appendChild(tr)
}

async function fetchSirens() {
    const data = await fetchAnyUrl(urlSirens);
    console.log("Sirens response:", data);
    sirenTable.innerHTML = ""
    data.forEach(insertSirens)
}



//Create modal
function openCreateModal() {
    // Clear all fields
    document.getElementById("createSirenName").value = "";
    document.getElementById("createSirenLatitude").value = "";
    document.getElementById("createSirenLongitude").value = "";
    document.getElementById("createSirenStatus").value = "PASSIVE";
    document.getElementById("createSirenDisabled").checked = false;

    createModal.style.display = "block";
}

//Create modal
function closeCreateModal() {
    createModal.style.display = "none";
}

//Update modal
function openUpdateModal(siren) {
    document.getElementById("sirenId").value = siren.id;
    document.getElementById("sirenName").value = siren.name;
    document.getElementById("sirenLatitude").value = siren.latitude;
    document.getElementById("sirenLongitude").value = siren.longitude;
    document.getElementById("sirenStatus").value = siren.status;
    document.getElementById("sirenDisabled").checked = siren.disabled;

    updateModal.style.display = "block"; // CHANGED FROM modal
}

// Close UPDATE modal - RENAMED FUNCTION
function closeUpdateModal() {
    updateModal.style.display = "none"; // CHANGED FROM modal
}

/**
 * Håndterer formularindsendelse for opdatering af siren.
 * Sender PUT-request til backend med opdaterede data.
 * @param {Event} e - Submit-event
 */

document.getElementById("updateSirenForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    //Bruger en mere manuel tilgang her, da checkboks og at lave Longitude og Latitude om til tal kan være besærligt med formdata.
    const sirenId = document.getElementById("sirenId").value;
    const name = document.getElementById("sirenName").value.trim();
    const latitude = document.getElementById("sirenLatitude").value;
    const longitude = document.getElementById("sirenLongitude").value;
    const status = document.getElementById("sirenStatus").value;
    const disabled = document.getElementById("sirenDisabled").checked;

    const updatedSiren = {
        name: name,
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        status: status,
        disabled: disabled
    };

    try {
        const response = await postObjectAsJson(`http://localhost:8080/sirens/siren/${sirenId}`,updatedSiren, "PUT");

        if (response.ok) {
            console.log("Siren blev opdateret!");
            closeUpdateModal(); // NOW THIS WORKS
            await fetchSirens();
        } else {
            console.error("Fik ikke skiftet status på siren:", response.status);
        }
    } catch (error) {
        console.error("Error under opdatering af siren:", error);
    }
});

/**
 * Håndterer formularindsendelse for oprettelse af ny siren.
 * Sender POST-request til backend med siren-data.
 * @param {Event} e - Submit-event
 */

document.getElementById("createSirenForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const formObject = Object.fromEntries(formData.entries());

    // laver værdierne om til tal fra string
    formObject.latitude = parseFloat(formObject.latitude);
    formObject.longitude = parseFloat(formObject.longitude);

    // Checkbox håndteres særskilt
    formObject.disabled = form.querySelector("#createSirenDisabled").checked;

    const newSiren = {
        name: formObject.name.trim(),
        latitude: formObject.latitude,
        longitude: formObject.longitude,
        status: formObject.status,
        disabled: formObject.disabled
    };

    try {
        const response = await postObjectAsJson("http://localhost:8080/sirens/create", newSiren, "POST");

        if (response.ok) {
            console.log("Siren blev skabt!");
            closeCreateModal();
            await fetchSirens();
        } else {
            console.error("Fejlede i at skabe sirenen");
        }
    } catch (error) {
        console.error("Error under skabelse af siren:", error);
    }
});

async function deleteSiren(sirenId) {
    if (confirm("Er du sikker på at du vil slette denne sirene?")) {
        try {
            const response = await fetch(`http://localhost:8080/sirens/siren/${sirenId}`, {
                method: "DELETE"
            });

            if (response.ok) {
                console.log("Sirenen slettet!");
                await fetchSirens();
            }
        } catch (error) {
            console.error("Error under sletning af siren:", error);
        }
    }
}

createSirenButton.addEventListener('click', openCreateModal);

// Close modals when clicking outside
window.onclick = function(event) {
    if (event.target == createModal) {
        closeCreateModal();
    }
    if (event.target == updateModal) { // CHANGED FROM modal
        closeUpdateModal();
    }
}

// Make close functions global so HTML can call them
window.closeCreateModal = closeCreateModal;
window.closeUpdateModal = closeUpdateModal; // NOW THIS WORKS

export {fetchSirens};
