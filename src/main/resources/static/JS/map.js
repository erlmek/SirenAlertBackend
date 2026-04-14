let map;
let sirenMarkers = [];
let fireMarkers = [];
let radiusCircles = [];

// Initialize map
function initMap() {
    // Center on LA
    map = L.map('map').setView([34.0522, -118.2437], 9);

    // Add tile layer (map background)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    console.log("Map initialized!");
}

// Clear all markers and circles
function clearMap() {
    sirenMarkers.forEach(marker => map.removeLayer(marker));
    fireMarkers.forEach(marker => map.removeLayer(marker));
    radiusCircles.forEach(circle => map.removeLayer(circle));

    sirenMarkers = [];
    fireMarkers = [];
    radiusCircles = [];
}

// Add siren markers
function addSirensToMap(sirens) {
    sirens.forEach(siren => {
        // Different colors based on status
        const color = siren.status === 'Fare' ? 'red' :
            siren.status === 'Fred' ? 'blue' : 'gray';

        const marker = L.circleMarker([siren.latitude, siren.longitude], {
            radius: 8,
            fillColor: color,
            color: 'white',
            weight: 2,
            opacity: 1,
            fillOpacity: 0.8
        }).addTo(map);

        // Popup with siren info
        marker.bindPopup(`
            <b>${siren.name}</b><br>
            Status: ${siren.status}<br>
            Disabled: ${siren.disabled ? 'Yes' : 'No'}
        `);

        sirenMarkers.push(marker);
    });
}

// Add fire markers with 10km radius
function addFiresToMap(fires) {
    fires.forEach(fire => {
        // Fire marker
        const marker = L.marker([fire.latitude, fire.longitude], {
            icon: L.divIcon({
                className: 'fire-marker',
                html: '🔥',
                iconSize: [20, 20]
            })
        }).addTo(map);

        // Popup with fire info
        const fireTime = new Date(fire.time).toLocaleString('da-DK');
        marker.bindPopup(`
            <b>${fire.name}</b><br>
            Status: ${fire.status ? 'Active' : 'Extinguished'}<br>
            Time: ${fireTime}
        `);

        fireMarkers.push(marker);

        // 10km radius circle (only for active fires)
        if (fire.status) {
            const circle = L.circle([fire.latitude, fire.longitude], {
                color: 'red',
                fillColor: '#ff0000',
                fillOpacity: 0.1,
                radius: 10000 // 10km in meters
            }).addTo(map);

            radiusCircles.push(circle);
        }
    });
}


async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    // If 204 No Content or empty body, return empty array
    if (response.status === 204) {
        return [];
    }
    const text = await response.text();
    if (!text) {
        return [];
    }
    return JSON.parse(text);
}

// Update map with fresh data
async function updateMap() {
    try {
        clearMap();

        // Fetch sirens and fires
        const sirens = await fetchJson("http://localhost:8080/sirens/all");
        const fires = await fetchJson("http://localhost:8080/fires/activeFires");

        addSirensToMap(sirens);
        addFiresToMap(fires);

        console.log("Map updated with", sirens.length, "sirens and", fires.length, "fires");
    } catch (error) {
        console.error("Failed to update map:", error);
    }
}

export { initMap, updateMap };
