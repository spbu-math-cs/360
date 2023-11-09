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
    demosJson.sort(function(d1, d2) {
        // Order:
        //  1. Active demos
        //  2. Future demos
        //  3. Past demos
        // If in same category, sort by eventId ascending
        function sign(x) {
            return (x > 0) ? 1 : ((x < 0) ? -1 : 0);
        }

        function f(x) {
            return (x == 0) ? 2 : ((x > 0) ? 1 : 0);
        }

        let a = calcTimeToDemo(d1), b = calcTimeToDemo(d2);
        if (sign(a) == sign(b)) {
            return sign(parseInt(d1["eventId"]) - parseInt(d2["eventId"]));
        } else {
            return sign(f(sign(b)) - f(sign(a)));
        }
    })

    demosJson.forEach(demo => {
        let id = demo["eventId"];
        let date = demo["date"]
        let timeToDemo = calcTimeToDemo(demo);
        let demoStatus = (timeToDemo < 0) ? "disabled" : ((timeToDemo > 0) ? "locked" : "unlocked");
        
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

    if (day < 20 && day > 10) {
        daySuffix == "th";
    } 

    return `<i>${day}<span>${daySuffix}</span> ${months[month]} ${year}</i>`;
}

function calcTimeToDemo(demo) {
    let date = demo["date"];
    let start = demo["start"];
    let finish = demo["finish"];

    // Parse to UNIX epoch time format
    let startUnix = Date.parse(date + 'T' + start + ":00.000+03:00");
    let finishUnix = Date.parse(date + 'T' + finish + ":00.000+03:00");
    let nowUnix = Date.now();

    if (nowUnix < startUnix) {
        return startUnix - nowUnix;
    } else if (nowUnix > finishUnix) {
        return finishUnix - nowUnix;
    } else {
        return 0;
    }
}