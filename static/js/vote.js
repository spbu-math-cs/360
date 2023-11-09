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

function lockVoteCard(teamId, success) {
    $(`#vote-card-team${teamId} :input`).prop("disabled", true);
    $(`#vote-card-team${teamId}`).addClass("locked-vote-card");
    $(`#grade-input-team${teamId}-btn`).addClass("hidden-vote-button");
    var text = success ? "You have successfully voted." : "You've voted for this team before.";
    var colorClass = success ? "text-success" : "text-primary";
    $(`#grade-status-team${teamId}`)
        .addClass("shown-grade-status")
        .addClass(colorClass)
        .removeClass("hidden-grade-status")
        .text(text);
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

function addVoteCards(teams, eventId) {
    console.log(teams);
    teams.forEach(team => {
        console.log(team);
        var teamId = team["teamId"];
        var teamNum = team["number"];
        var teamName = team["name"];
        var projectName = team["projectName"];

        $("#voting-cards").append(
        `
<form id="voting-card-team${teamId}" class="voting-card" onsubmit="voteSubmit(this)">
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
    <button class="black-button" type="submit">Vote</button>
</form>
        `
        );

        $(`#vote-card-team${teamId}`).css({"opacity": "0", "z-index" : "0"});

        $(`#vote-card-team${teamId}`).submit(function(event) {
            event.preventDefault();
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
                    // lockVoteCard(teamId, true);
                    console.log("success");
                } else if (response.status == 409 /* HttpStatusCode.Conflict "You have already rated!" */){
                    // lockVoteCard(teamId, false);
                    console.log("already voted");
                } else {
                    response.text().then(text => alert(text));
                }
            });
        });
    });

    $(`#voting-card-team1`).addClass("active");
    $(`#voting-button-team1`).addClass("active");

}

var selectedCardId = "voting-card-team1";
var selectedButton = "voting-button-team1"; 

function selectTeam(button) {
    let cardId = $(button).attr("data-card-id");
    let buttonId = $(button).attr("id");

    $('#' + selectedCardId).removeClass("active");
    $('#' + cardId).addClass("active");

    $('#' + selectedButton).removeClass("active");
    $('#' + buttonId).addClass("active");

    selectedCardId = cardId;
    selectedButton = buttonId;
}