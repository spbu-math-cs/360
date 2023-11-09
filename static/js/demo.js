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
            <p class="demo-card-date">${dateToHumanFormat(date)}</p>
        </div>
        `);
    });
}

function dateToHumanFormat(date) {
    let date_s = date.split('-');
    let day = parseInt(date_s[2]);
    let month = date_s[1];
    let year = date_s[0];

    var months = {
        "01" : "Jan.",
        "02" : "Feb.",
        "03" : "Mar.",
        "04" : "Apr.",
        "05" : "May",
        "06" : "Jun.",
        "07" : "Jul.",
        "08" : "Aug.",
        "09" : "Sep.",
        "10" : "Oct.",
        "11" : "Nov.",
        "12" : "Dec.",
    };

    let daySuffix;
    switch (day % 10) {
        case 1:
            daySuffix = "st";
            break;
        case 2:
            daySuffix = "nd";
            break;
        case 3:
            daySuffix = "rd";
            break;
        default:
            daySuffix = "th";
            break;
    }

    return `<i>${day}<span>${daySuffix}</span> ${months[month]} ${year}</i>`;
}