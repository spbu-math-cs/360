$(document).ready(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to vote!");
        window.location.href = "/demo";
        return;
    }
    getTeamsOnDemo(parseInt(eventId));
})

function lockVoteCard(teamId) {
    $(`#voting-card-team${teamId} input, #voting-card-team${teamId} textarea`).prop("disabled", true);
    $(`#voting-card-team${teamId}`).addClass("voted");
    $(`#vote-button-team${teamId}`).hide();
    $(`#revote-button-team${teamId}`).show();

    $(`#voting-button-team${teamId}`).addClass("voted");
}

function unlockVoteCard(teamId) {
    $(`#voting-card-team${teamId} input, #voting-card-team${teamId} textarea`).prop("disabled", false);
    $(`#voting-card-team${teamId}`).removeClass("voted");
    $(`#vote-button-team${teamId}`).show();
    $(`#revote-button-team${teamId}`).hide();

    $(`#voting-button-team${teamId}`).removeClass("voted");
}

function getTeamsOnDemo(eventId) {
    console.log("demoId = " + eventId);
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

    lockVoteCard(teamId);

    // fetch('/demo/vote', {
    //     method: 'POST',
    //     headers: {
    //         'Accept': 'application/json',
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify(
    //         {
    //             eventId: eventId,
    //             teamId: teamId,
    //             level: grade_1,
    //             grade: grade_2,
    //             presentation: grade_3,
    //             additional: grade_4,
    //             comment: comment
    //         }
    //     )
    // })
    // .then(response => {
    //     if (response.ok) {
    //         lockVoteCard(teamId);
    //     } else {
    //         response.text().then(text => alert(text));
    //     }
    // });
}

function addVoteCards(teams, eventId) {
    teams.forEach(team => {
        var teamId = team["teamId"];
        var teamNum = team["number"];
        var teamName = team["name"];
        var projectName = team["projectName"];

        $("#voting-cards").append(
        `
<div id="voting-card-team${teamId}" class="voting-card">
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
    <button id="vote-button-team${teamId}" class="black-button" type="button" onclick="vote(${teamId}, ${eventId})">Vote</button>
    <button id="revote-button-team${teamId}" class="white-button" type="button" onclick="unlockVoteCard(${teamId})">Revote</button>
</div>
        `
        );

        $(`#vote-card-team${teamId}`).css({"opacity": "0", "z-index" : "0"});
        $(`#revote-button-team${teamId}`).hide();

        $(`#voting-buttons`).append(`
            <div class="team-button" id="voting-button-team${teamId}" onclick="showCard('voting-card-team${teamId}', 'voting-button-team${teamId}')">${teamNum}</div>
        `);

        $("#graph").append(`
        <div class="item" id="graph-team${teamId}" style="--val: 0; --clr: #ffffff;">
            <div class="label">${teamNum}</div>
            <div class="value">0%</div>
        </div>
        `);
    });

    $(`#voting-buttons`).append(`
        <div class="graphs-button" id="graphs-button" onclick="updateGraph();showCard('graphs-card', 'graphs-button')">
            <img src="../img/chart.png" alt="Graphs">
        </div>
    `);

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
