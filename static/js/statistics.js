$(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to view statistics!");
        window.location.href = "/demo";
        return;
    }

    $(`#statistics-content > h1`).html(`Demo ${eventId}`);

    fetchRank(eventId);
});

function fetchRank(eventId) {
    fetch(`/profile/info`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                if (responseJson["rank"] == "teacher") {
                    fetchTeams(eventId, responseJson);
                } else {
                    fillPage("#statistics-content", responseJson, eventId, true, [], 1);
                }
            })
        } else {
            response.text().then(text => function() { alert(text) });
        }
    });
}

function fetchTeams(eventId, userInfo) {
    fetch(`/demo/vote/teams?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                fillPage("#statistics-content", userInfo, eventId, false, responseJson, 1);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}