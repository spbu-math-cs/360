$(document).ready(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to vote!");
        window.location.href = "/demo";
        return;
    }

    fetchTeams(parseInt(eventId));
})

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
            response.json().then(responseJson => {
                addVoteCards(responseJson, eventId);
            })
        } else if (response.status == 409) {
            alert("You must be on a team to vote.");
            location.href = "/demo";
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
            <img src="../img/chart.png" alt="Graphs">
        </div>
    `);

    $(`#lower-voting-buttons`).append(`
        <div class="voting-button statistics-button" id="statistics-button" onclick="showCard('statistics-card', 'statistics-button')">
            Statistics
        </div> 
    `);

    fetchInteamVoting(parseInt(eventId));
}

function fetchInteamVoting(eventId) {
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
                fetchId(responseJson, eventId);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function fetchId(team, eventId) {
    fetch(`/profile/id`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(async (response) => {
        if (response.ok) {
            addInteamVotingCard(team, eventId, await response.json());
        } else {
            alert(await response.text());
        }
    });
    
}

function addInteamVotingCard(team, eventId, response) {
    var i = 1;
    var currentUserId = response["userId"];
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

    preparePage(eventId);
}

function preparePage(eventId) {
    $(`.card`).css({"opacity": "0", "z-index" : "-1"});
    $(`.revote-button`).hide();

    $(`input[type="range"]`).each(function() {
        $(this).css("--_value", `"${$(this).val()}"`)
    });
    
    $(`input[type="range"]`).on("input", function() {
        $(this).css("--_value", `"${$(this).val()}"`);
    });

    updateRealTimeStatistics(eventId);
    setInterval(function() { updateRealTimeStatistics(eventId) }, 10000);

    fetchPreviousGrades(eventId);
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

function fetchPreviousGrades(eventId) {
    fetch(`/demo/vote/grades?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(async (response) => {
        if (response.ok) {
            fillPreviousGrades(await response.json());
        }
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
        lockVoteCard(`#voting-card-team${teamId}`, `#voting-button-team${teamId}`);
    });
}

var selectedCardId = "voting-card-team1";
var selectedButton = "voting-button-team1";

function showCard(cardId, buttonId) {
    $('#' + selectedCardId).removeClass("active");
    $('#' + cardId).addClass("active");

    $('#' + selectedButton).removeClass("active");
    $('#' + buttonId).addClass("active");

    selectedCardId = cardId;
    selectedButton = buttonId;
}

function calcColorBetween(color1, color2, ratio) {
    var hex = function(x) {
        x = x.toString(16);
        return (x.length == 1) ? '0' + x : x;
    };

    var r = Math.ceil(parseInt(color1.substring(0,2), 16) * ratio + parseInt(color2.substring(0,2), 16) * (1-ratio));
    var g = Math.ceil(parseInt(color1.substring(2,4), 16) * ratio + parseInt(color2.substring(2,4), 16) * (1-ratio));
    var b = Math.ceil(parseInt(color1.substring(4,6), 16) * ratio + parseInt(color2.substring(4,6), 16) * (1-ratio));

    return hex(r) + hex(g) + hex(b);
}

MAX_GRADE = 20.0

function calcGrade(teamId) {
    var grade_1 = parseInt($(`#grade-input-team${teamId}-1`).val());
    var grade_2 = parseInt($(`#grade-input-team${teamId}-2`).val());
    var grade_3 = parseInt($(`#grade-input-team${teamId}-3`).val());
    var grade_4 = parseInt($(`#grade-input-team${teamId}-4`).val());
    return (grade_1 + grade_2 + grade_3) * (1.0 + grade_4 / 9.0);
}

function updateGraph() {
    $(`.grades-graph > .item`).each(function() {
        var teamId = parseInt($(this).attr("id").substring(10));
        var grade = calcGrade(teamId);
        var ratio = grade / MAX_GRADE;
        var color = calcColorBetween("92DF7E", "12486B", ratio);
        $(this).attr("style", `--val: ${ratio * 100}; --clr: #${color};`);
        $(this).children(".value").html(`${Math.round(grade * 10) / 10}`);
    });
}

function inTeamVote(eventId) {
    var voteResult = [];
    $(`#in-team-voting-card > input`).each(function() {
        var grade = $(this).val();
        var UID = $(this).attr("-data-UID");
        voteResult.push({"UID": UID, "grade": grade});
    });

    fetch('/demo/vote', {
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

    $(`#upper-voting-buttons`).append(`
        <div class="voting-button graphs-button" id="graphs-button" onclick="updateGraph();showCard('graphs-card', 'graphs-button')">
            <img src="../img/chart.png" alt="Graphs">
        </div>
    `);

    $(`.card`).css({"opacity": "0", "z-index" : "0"});
    $(`.revote-button`).hide();

    showCard(`voting-card-team1`, `voting-button-team1`);
}

function showCard(cardId, buttonId) {
    $('.card').removeClass("active");
    $('.voting-button').removeClass("active");

    $('#' + cardId).addClass("active");
    $('#' + buttonId).addClass("active");
}

function calcColorBetween(color1, color2, ratio) {
    var hex = function(x) {
        x = x.toString(16);
        return (x.length == 1) ? '0' + x : x;
    };

    var r = Math.ceil(parseInt(color1.substring(0,2), 16) * ratio + parseInt(color2.substring(0,2), 16) * (1-ratio));
    var g = Math.ceil(parseInt(color1.substring(2,4), 16) * ratio + parseInt(color2.substring(2,4), 16) * (1-ratio));
    var b = Math.ceil(parseInt(color1.substring(4,6), 16) * ratio + parseInt(color2.substring(4,6), 16) * (1-ratio));

    return hex(r) + hex(g) + hex(b);
}

MAX_GRADE = 20.0

function calcGrade(teamId) {
    var grade_1 = parseInt($(`#grade-input-team${teamId}-1`).val());
    var grade_2 = parseInt($(`#grade-input-team${teamId}-2`).val());
    var grade_3 = parseInt($(`#grade-input-team${teamId}-3`).val());
    var grade_4 = parseInt($(`#grade-input-team${teamId}-4`).val());
    return (grade_1 + grade_2 + grade_3) * (1.0 + grade_4 / 9.0);
}

function updateGraph() {
    $(`.grades-graph > .item`).each(function() {
        var teamId = parseInt($(this).attr("id").substring(10));
        var grade = calcGrade(teamId);
        var ratio = grade / MAX_GRADE;
        var color = calcColorBetween("92DF7E", "12486B", ratio);
        $(this).attr("style", `--val: ${ratio * 100}; --clr: #${color};`);
        $(this).children(".value").html(`${Math.round(grade * 10) / 10}`);
    });
}

function inTeamVote(eventId) {
    var voteResult = [];
    $(`#in-team-voting-card > input`).each(function() {
        var grade = $(this).val();
        var UID = $(this).attr("-data-UID");
        voteResult.push({"personId": UID, "grade": grade});
    });
    
    // console.log(voteResult);
    // lockVoteCard(`#in-team-voting-card`, `#in-team-voting-button`);

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