$(function() {
    if (getPageName() == "demo") {
        fetch('/demo_list', {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(response => setDemosInPage(response));
    }
});

function setDemosInPage(demosJson) {
    demosJson.forEach(demo => {
        let id = demo["eventId"];
        let date = demo["date"];
        let start = demo["start"];
        let finish = demo["finish"];

        let date_s = date.split('-');
        // Reverse, because var date in yyyy-mm-dd, and Date.parse needs dd-mm-yyyy
        let date_f = date_s[0] + '-' + date_s[1] + '-' + date_s[2];

        // Parse to UNIX epoch time format
        let st = Date.parse(date_f + 'T' + start + ":00.000+03:00");
        let fn = Date.parse(date_f + 'T' + finish + ":00.000+03:00");
        let now = Date.now();

        // If too early, then locked, if too late, then disabled, else nothing (opened)
        let demoStatus = now < st ? "locked" : (now > fn ? "disabled" : "unlocked");

        $("#demo-container").append(
        `
        <div class="demo-card ${demoStatus}" ${demoStatus == "unlocked" ? `onclick="location.href='/demo/vote?eventId=${id}'"` : ""}>
            <h2 class="demo-card-title">Demo ${id}</h2>
            <h3 class="demo-card-date">${date}</h3>
        </div>
        `);
    });
}