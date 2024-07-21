function getCountryCode() {
    const country = document.getElementById("countrySelect").value;
    fetch(`/getCode?country=${country}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("countryCode").innerText = `Country Code: ${data}`;
        });
}
