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

    fetchRankStatistics(eventId);

    // Previous InTeam votings feature
    // fetchInteamVoting_Feature(eventId, profileInfoResponse);
    // fetchInteamPreviousGrades(eventId);
});

var profileInfoFetched = false;
var profileInfoResponse = null;

function fetchRankStatistics(eventId) {
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
                    fetchTeamsStatistics(eventId, responseJson);
                } else {
                    fillPage("#statistics-content", responseJson, eventId, true, [], 1, true);
                    profileInfoResponse = responseJson;
                    profileInfoFetched = true;
                }
            })
        } else {
            response.text().then(text => function() { alert(text) });
        }
    });
}

function fetchTeamsStatistics(eventId, userInfo) {
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
                fillPage("#statistics-content", userInfo, eventId, false, responseJson, 1, false);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function fetchInteamVoting_Feature(eventId) {
    fetch(`/profile/team/info`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                waitFor(() => profileInfoFetched == true, () => addInteamVotingCard_Feature(responseJson, eventId, profileInfoResponse));
            })
        } else {
		$("#in-team-voting-card").remove();
        }
    });
}

function addInteamVotingCard_Feature(team, eventId, response) {
    var i = 1;
    var currentUserId = response["id"];
    team["members"].forEach(member => {
        if (member["user_id"] != currentUserId) {
            $("#in-team-voting-card").append(`
                <label for="grade-member-${i}">${member["last_name"]} ${member["first_name"]}</label>
                <input class="grade" type="range" -data-UID="${member["user_id"]}" id="grade-member-${i}" min="0" max="10" value="5">
            `);
            i += 1;
        }
    });

    $("#in-team-voting-card").append(`
        <button class="vote-button black-button" type="button" onclick="inTeamVote(${eventId})">Vote</button>
        <button class="revote-button white-button" type="button" onclick="unlockVoteCard('#in-team-voting-card', '#in-team-voting-button')">Revote</button>
    `);

    $(`#in-team-voting-card > .revote-button`).hide();
    inteamCardCreated = true;

    $(`input[type="range"]`).each(function() {
        $(this).css("--_value", `"${$(this).val()}"`)
    });
    
    $(`input[type="range"]`).on("input", function() {
        $(this).css("--_value", `"${$(this).val()}"`);
    });
}
