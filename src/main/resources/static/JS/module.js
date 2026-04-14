function fetchAnyUrl(url) {
    return fetch(url).then(response => response.json()).catch(error => console.error("Opstod fejl", error))
}


async function postObjectAsJson(url, body, httpRequest) {
    const response = await fetch(url, {
        method: httpRequest,
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        const errorMessage = await response.text();
        console.error(errorMessage);
    }
    return response;
}
export {fetchAnyUrl, postObjectAsJson};