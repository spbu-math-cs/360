$(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to view statistics!");
        window.location.href = "/demo";
        return;
    }

    fetchStatictics(eventId);
});

function fetchStatictics(eventId) {
    fetch(`/demo/statistics/average?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                setStatistics(responseJson, eventId);
            })
        } else {
            response.text().then(text => function() {
                alert("There's no statistics for this demo.");
                window.location.href = "/demo";
            });
        }
    });
}


function normalize(value, lBound, rBound) {
    return Math.min(Math.max((value - lBound) / (rBound - lBound), 0), 1);
}

function setStatistics(statistics, eventId) {
    $(`.statistics-content > h1`).html(`Demo ${eventId}`);
    
    $(`#ratio-1`).attr("style", `--ratio: ${normalize(statistics["avgLevel"], 1, 5)}`);
    $(`#ratio-1 > p`).html(Math.floor(statistics["avgLevel"] * 10) / 10);
    $(`#ratio-2`).attr("style", `--ratio: ${normalize(statistics["avgGrade"], 1, 5)}`);
    $(`#ratio-2 > p`).html(Math.floor(statistics["avgGrade"] * 10) / 10);
    $(`#ratio-3`).attr("style", `--ratio: ${normalize(statistics["avgPresentation"], 1, 5)}`);
    $(`#ratio-3 > p`).html(Math.floor(statistics["avgPresentation"] * 10) / 10);
    $(`#ratio-4`).attr("style", `--ratio: ${normalize(statistics["avgAdditional"], 0, 3)}`);
    $(`#ratio-4 > p`).html(Math.floor(statistics["avgAdditional"] * 10) / 10);

    fetchComments(eventId);
}

function fetchComments(eventId) {
    fetch(`/demo/statistics/comments?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                setComments(responseJson);
            })
        }
    });
}

function setComments(comments) {
    comments.forEach(comment => {
        $(`#comments-container`).append(`
        <div class="comment">
            <h3>${htmlEncode(comment["lastName"])} ${htmlEncode(comment["firstName"])} ${htmlEncode(comment["fatherName"])}</h3>
            <p>${htmlEncode(comment["comment"])}</p>
        </div>
        `);
    });
}

function htmlEncode(str){
    return String(str).replace(/[^\w. ]/gi, function(c){
        return '&#'+c.charCodeAt(0)+';';
    });
}