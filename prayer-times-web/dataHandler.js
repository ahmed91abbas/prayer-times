const url = 'https://raw.githubusercontent.com/ahmed91abbas/prayer-times/gh-pages/prayer-times-web/prayer-times-malmo.json';


function writeToDocument(data) {
    data.forEach((month) => {
        month.forEach((day) => {
            document.write(JSON.stringify(day, null, 2));
            document.write('<br>');
        })
        document.write('<br>');
    })
}

fetch(url)
    .then((response) => response.json())
    .then((responseJSON) => {
        writeToDocument(responseJSON);
    });
