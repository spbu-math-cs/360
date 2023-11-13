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
                addInteamVotingCard(responseJson, eventId);
            })
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function addInteamVotingCard(team, eventId) {
    var i = 1;
    JSON.parse(team["members"]).forEach(member => {
        $("#in-team-voting-card").append(`
            <label for="grade-member-${i}">${member["last_name"]} ${member["first_name"]}</label>
            <input class="grade" type="range" -data-UID="${member["user_id"]}" id="grade-member-${i}" min="1" max="5">
        `);
        i += 1;
    });

    $("#in-team-voting-card").append(`
        <button class="vote-button black-button" type="button" onclick="inTeamVote(${eventId})">Vote</button>
        <button class="revote-button white-button" type="button" onclick="unlockVoteCard('#in-team-voting-card', '#in-team-voting-button')">Revote</button>
    `);

    preparePage();
}

function preparePage() {
    $(`.card`).css({"opacity": "0", "z-index" : "0"});
    $(`.revote-button`).hide();

    $(`#voting-card-team1`).addClass("active");
    $(`#voting-button-team1`).addClass("active");
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

    $(`#voting-card-team1`).addClass("active");
    $(`#voting-button-team1`).addClass("active");

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
    
    console.log(voteResult);
    lockVoteCard(`#in-team-voting-card`, `#in-team-voting-button`);

    // fetch('/demo/vote', {
    //     method: 'POST',
    //     headers: {
    //         'Accept': 'application/json',
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify(
    //         {
    //             eventId: eventId,
    //             grades: voteResult
    //         }
    //     )
    // })
    // .then(response => {
    //     if (response.ok) {
    //         lockVoteCard(`#in-team-voting-card`, `#in-team-voting-button`);
    //     } else {
    //         response.text().then(text => alert(text));
    //     }
    // });
}