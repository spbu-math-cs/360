$(document).ready(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    if (url.pathname == "/demo/vote") {
        var eventId = parseInt(url.searchParams.get("eventId"));
        if (eventId == null) {
            window.alert("You haven't choose demo to vote!");
            window.location.href = "/demo";
            return;
        }

        fetchTeams(eventId);
        fetchPreviousGrades(eventId);
        fetchInteamPreviousGrades(eventId);
        fetchId(eventId);
    }
})

var voteCardsCreated = false;
var inteamCardCreated = false;
var teamsFetched = false;
var teamsResponse = null;

function lockVoteCard(cardId, buttonId) {
    $(`${cardId} input, ${cardId} textarea`).prop("disabled", true);
    $(`${cardId}`).addClass("voted");
    $(`${cardId} .vote-button`).hide();
    $(`${cardId} .revote-button`).show();

    $(`${buttonId}`).addClass("voted");
}

function unlockVoteCard(cardId, buttonId) {
    $(`${cardId} input, ${cardId} textarea`).prop("disabled", false);
    $(`${cardId}`).removeClass("voted");
    $(`${cardId} .vote-button`).show();
    $(`${cardId} .revote-button`).hide();

    $(`${buttonId}`).removeClass("voted");
}

function fetchTeams(eventId) {
    fetch(`/demo/vote/teams?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(teams => {
                teamsFetched = true;
                teamsResponse = teams;
                addVoteCards(teams, eventId);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function vote(teamId, eventId) {
    var grade_1 = parseInt($(`#grade-input-team${teamId}-1`).val());
    var grade_2 = parseInt($(`#grade-input-team${teamId}-2`).val());
    var grade_3 = parseInt($(`#grade-input-team${teamId}-3`).val());
    var grade_4 = parseInt($(`#grade-input-team${teamId}-4`).val());
    var comment = $(`#grade-comment-team${teamId}`).val();

    fetch('/demo/vote', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(
            {
                eventId: eventId,
                teamId: teamId,
                level: grade_1,
                grade: grade_2,
                presentation: grade_3,
                additional: grade_4,
                comment: comment
            }
        )
    })
    .then(response => {
        if (response.ok) {
            lockVoteCard(`#voting-card-team${teamId}`, `#voting-button-team${teamId}`);
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function addVoteCards(teams, eventId) {
    teams.forEach(team => {
        var teamId = team["teamId"];
        var teamNum = team["number"];
        var teamName = team["name"];
        var projectName = team["projectName"];

        $("#voting-cards").append(
        `
<div id="voting-card-team${teamId}" class="voting-card card">
    <h1>Team ${teamNum}</h1>
    <h3>${projectName}</h3>
    <label for="grade-input-team${teamId}-1">Сложность спринта</label>
    <input class="grade" type="range" id="grade-input-team${teamId}-1" min="1" max="5">
    <label for="grade-input-team${teamId}-2">Уровень выполнения</label>
    <input class="grade" type="range" id="grade-input-team${teamId}-2" min="1" max="5">
    <label for="grade-input-team${teamId}-3">Качество презентации</label>
    <input class="grade" type="range" id="grade-input-team${teamId}-3" min="1" max="5">
    <label for="grade-input-team${teamId}-4">Дополнительные баллы</label>
    <input class="bonus-grade" type="range" id="grade-input-team${teamId}-4" min="0" max="3" value="0">
    <label for="grade-comment-team${teamId}">Комментарий</label>
    <textarea id="grade-comment-team${teamId}" rows="2"></textarea>
    <button class="vote-button black-button" type="button" onclick="vote(${teamId}, ${eventId})">Vote</button>
    <button class="revote-button white-button" type="button" onclick="unlockVoteCard('#voting-card-team${teamId}', '#voting-button-team${teamId}')">Revote</button>
</div>
        `
        );

        $(`#upper-voting-buttons`).append(`
            <div class="voting-button team-button" id="voting-button-team${teamId}" onclick="showCard('voting-card-team${teamId}', 'voting-button-team${teamId}')">${teamNum}</div>
        `);

        $("#graph").append(`
        <div class="item" id="graph-team${teamId}" style="--val: 0; --clr: #ffffff;">
            <div class="label">${teamNum}</div>
            <div class="value">0%</div>
        </div>
        `);
    });

    $(`#upper-voting-buttons`).append(`
        <div class="voting-button graphs-button" id="graphs-button" onclick="updateGraph();showCard('graphs-card', 'graphs-button')">
            <img src="/img/chart.png" alt="Graphs">
        </div>
    `);

    $(`#lower-voting-buttons`).append(`
        <div class="voting-button statistics-button" id="statistics-button" onclick="showCard('statistics-card', 'statistics-button')">
            Statistics
        </div> 
    `);

    $(`.revote-button`).hide();

    if ($(".card.active").length == 0) {
        showCard(`voting-card-team1`, `voting-button-team1`);
    }

    voteCardsCreated = true;
}

function fetchInteamVoting(eventId, profileInfo) {
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
                addInteamVotingCard(responseJson, eventId, profileInfo);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function fetchId(eventId) {
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
                fillPage("#statistics-container", responseJson, eventId, false, teamsResponse, 0);
                if (responseJson["rank"] == "teacher") {
                    $(`#in-team-voting-button`).css("display", "none");
                    waitFor(() => teamsFetched == true, () => {
                        updateAllStatistics(eventId, teamsResponse, 0);
                        setInterval(function() { updateAllStatistics(eventId, teamsResponse, 0); }, 10000);
                        preparePage(eventId);
                    });
                } else {
                    fetchStatistics(eventId, false, 0);
                    setInterval(function() { fetchStatistics(eventId, false, 0); }, 10000);
                    fetchInteamVoting(eventId, responseJson);
                }
            });
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function addInteamVotingCard(team, eventId, response) {
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

    preparePage(eventId);
}

function preparePage(eventId) {
    $(`.card`).css({"opacity": "0", "z-index" : "-1"});

    $(`input[type="range"]`).each(function() {
        $(this).css("--_value", `"${$(this).val()}"`)
    });
    
    $(`input[type="range"]`).on("input", function() {
        $(this).css("--_value", `"${$(this).val()}"`);
    });
}

function updateRealTimeStatistics(eventId) {
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
                setRealTimeStatistics(responseJson);
            })
        }
    });
}

function normalize(value, lBound, rBound) {
    return Math.min(Math.max((value - lBound) / (rBound - lBound), 0), 1);
}

function setRealTimeStatistics(statistics) {
    $(`#ratio-1`).attr("style", `--ratio: ${normalize(statistics["avgLevel"], 1, 5)}`);
    $(`#ratio-1 > p`).html(Math.floor(statistics["avgLevel"] * 10) / 10);
    $(`#ratio-2`).attr("style", `--ratio: ${normalize(statistics["avgGrade"], 1, 5)}`);
    $(`#ratio-2 > p`).html(Math.floor(statistics["avgGrade"] * 10) / 10);
    $(`#ratio-3`).attr("style", `--ratio: ${normalize(statistics["avgPresentation"], 1, 5)}`);
    $(`#ratio-3 > p`).html(Math.floor(statistics["avgPresentation"] * 10) / 10);
    $(`#ratio-4`).attr("style", `--ratio: ${normalize(statistics["avgAdditional"], 0, 3)}`);
    $(`#ratio-4 > p`).html(Math.floor(statistics["avgAdditional"] * 10) / 10);
} 

function waitFor(pred, func) {
    if (pred()) func();
    else setTimeout(() => waitFor(pred, func), 250);
}

function fetchPreviousGrades(eventId) {
    fetch(`/demo/vote/grades?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(prevGrades => {
                waitFor(() => voteCardsCreated == true, () => fillPreviousGrades(prevGrades));
            });
        }
    });
}

function touchInputs() {
    $(`input[type="range"]`).each(function() {
        $(this).css("--_value", `"${$(this).val()}"`)
    });
}

function fillPreviousGrades(grades) {
    grades.forEach(grade => {
        var teamId = grade["teamId"];
        $(`#voting-button-team${teamId}`).addClass('voted');
        $(`#grade-input-team${teamId}-1`).val(grade["level"]);
        $(`#grade-input-team${teamId}-2`).val(grade["grade"]);
        $(`#grade-input-team${teamId}-3`).val(grade["presentation"]);
        $(`#grade-input-team${teamId}-4`).val(grade["additional"]);
        $(`#grade-comment-team${teamId}`).val(grade["comment"]);
        touchInputs();
        lockVoteCard(`#voting-card-team${teamId}`, `#voting-button-team${teamId}`);
    });
}

function fetchInteamPreviousGrades(eventId) {
    fetch(`/demo/vote/inteam/grades?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(inteamPrevGrades => {
                waitFor(() => inteamCardCreated == true, () => fillInteamPreviousGrades(inteamPrevGrades));
            });
        }
    });
}

function fillInteamPreviousGrades(grades) {
    if (grades.length != 0) {
        $(`#in-team-voting-card > input`).each(function() {
            var UID = $(this).attr("-data-UID");
            grades.filter(grade => grade["personId"] == UID).forEach(grade => {
                $(this).val(grade["grade"]);
            });
        });
        touchInputs();
        lockVoteCard(`#in-team-voting-card`, `#in-team-voting-button`);
    }
}

function showCard(cardId, buttonId) {
    $('.card').removeClass("active");
    $('.voting-button').removeClass("active");

    $('#' + cardId).addClass("active");
    $('#' + buttonId).addClass("active");
}

function inTeamVote(eventId) {
    var voteResult = [];
    $(`#in-team-voting-card > input`).each(function() {
        var grade = $(this).val();
        var UID = $(this).attr("-data-UID");
        voteResult.push({"personId": UID, "grade": grade});
    });

    fetch('/demo/vote/inteam', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(
            {
                eventId: eventId,
                grades: voteResult
            }
        )
    })
    .then(response => {
        if (response.ok) {
            lockVoteCard(`#in-team-voting-card`, `#in-team-voting-button`);
        } else {
            response.text().then(text => alert(text));
        }
    });
}