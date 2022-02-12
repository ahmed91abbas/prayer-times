const url = 'https://raw.githubusercontent.com/ahmed91abbas/prayer-times/main/prayer-times-web/prayer-times-malmo.json';

function loadTableData(data) {
    const table = document.getElementById("timesBody");
    const month = data[0];
    month.forEach((day, i) => {
        let row = table.insertRow();
        let dayNbr = row.insertCell(0);
        dayNbr.innerHTML = i + 1;
        day.forEach((time, j) => {
            let cell = row.insertCell(j + 1);
            cell.innerHTML = `${time.slice(0, 2)}:${time.slice(2)}`;
        })
    })
}

fetch(url)
    .then((response) => response.json())
    .then((responseJSON) => {
        loadTableData(responseJSON);
    });
