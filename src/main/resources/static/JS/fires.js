import {fetchAnyUrl,postObjectAsJson} from "./module.js";
import {fetchSirens} from "./sirens.js";
import {initMap, updateMap} from "./map.js";

const urlFire = "http://localhost:8080/fires/activeFires"
const fireTable = document.getElementById("FireTableBody");
const fireButton = document.getElementById("brandKnap")

/**
 * Indsætter en række i brand-tabellen for hver brand.
 * Viser brandens oplysninger og en knap til at opdatere status.
 * @param {Object} fire - Brandobjekt med data
 */

function insertFires(fire) {
    const tr = document.createElement("tr")
    tr.id = "Brand" + fire.id;

    // Formater tidspunkt til dansk dato- og tidsformat
    const formattedTime = new Date(fire.time).toLocaleString('da-DK', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });

    tr.innerHTML =
        "<td>" + fire.id + "</td>" +
        "<td>" + fire.name + "</td>" +
        "<td>" + fire.latitude.toFixed(2) + "</td>" +
        "<td>" + fire.longitude.toFixed(2) + "</td>" +
        "<td>" + formattedTime + "</td>" +
        "<td>" + (fire.status ? "Aktiv" : "Slukket") + "</td>"

    const cell = document.createElement("td");
    const updateButton = document.createElement("input");
    updateButton.type = "button";
    updateButton.setAttribute("value", fire.status ? "Sluk brand" : "Tænd brand");

    updateButton.onclick = async () => {
        const newStatus = !fire.status;

        try {
            await postObjectAsJson("http://localhost:8080/fires/fires/" + fire.id, newStatus, "PUT");
            await fetchFire(); // Opdatere fire table
            await fetchSirens(); // Opdattere siren table
        } catch (error) {
            console.error("Fejlede i at opdate branden:", error);
        }
    }
    cell.appendChild(updateButton);
    tr.appendChild(cell);
    fireTable.appendChild(tr)
}

async function fetchFire() {
    try {
        const data = await fetchAnyUrl(urlFire);
        fireTable.innerHTML = ""
        data.forEach(insertFires)
    } catch (error) {
        console.error("Fejlede med at hente brænde enheder:", error);
    }
}

/**
 * Opretter en ny tilfældig brand via backend og opdaterer UI.
 */

async function createFire() {
    try {
        const response = await fetch("http://localhost:8080/fires/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            console.log("Der er Ild!");
            await fetchFire();
            await fetchSirens();
            await updateMap();
        } else {
            console.error("Fejlede med at skabe brand");
        }
    } catch (error) {
        console.error("Fejlbesked under brand skabelse:", error);
    }
}

// Henter brænde og sirener når program åbnes
document.addEventListener("DOMContentLoaded", async () => {
    initMap()
    await fetchFire();
    await fetchSirens();
    await updateMap();
});

fireButton.addEventListener('click', createFire);
