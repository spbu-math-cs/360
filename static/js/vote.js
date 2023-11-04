$(document).ready(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to vote!");
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
    teams.forEach(team => {
        var teamId = team["teamId"];
        var teamNum = team["number"];
        var teamName = team["name"];
        var projectName = team["projectName"];
        $("#vote-cards").append(
        `
        <div id="vote-card-team${teamId}" class="col border rounded text-center justify-content-between p-3">
            <form id="vote-card-form-team${teamId}">
                <h3 class="my-2">Team ${teamNum}</h3>
                <h5 class="my-1 mx-3">${teamName}</h5>
                <p class="my-1 mx-3">${projectName}</p>
                <label for="grade-input-team${teamId}-1" class="form-label mt-2">Сложность спринта</label>
                <input type="range" name="grade-input-1" class="form-range" id="grade-input-team${teamId}-1" min="1" max="5">
                <label for="grade-input-team${teamId}-2" class="form-label mt-2">Уровень выполнения</label>
                <input type="range" name="grade-input-2" class="form-range" id="grade-input-team${teamId}-2" min="1" max="5">
                <label for="grade-input-team${teamId}-3" class="form-label mt-2">Качество презентации</label>
                <input type="range" name="grade-input-3" class="form-range" id="grade-input-team${teamId}-3" min="1" max="5">
                <label for="grade-input-team${teamId}-4" class="form-label mt-2">Дополнительные баллы</label>
                <input type="range" name="grade-input-4" class="form-range w-50" id="grade-input-team${teamId}-4" min="0" max="3" value="0">
                <div class="form-group">
                    <label for="grade-comment-team${teamId}" class="mt-2">Комментарий</label>
                    <textarea class="form-control my-2" id="grade-comment-team${teamId}" rows="2"></textarea>
                </div>
                <p id=grade-status-team${teamId} class="hidden-grade-status fst-italic py-2 my-3"></p>
                <input class="btn btn-outline-primary w-75 py-2 my-3" type="submit" id="grade-input-team${teamId}-btn" value="Vote">
            </form>
        </div>
        `
        );
        $(`#vote-card-form-team${teamId}`).submit(function(event) {
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
                    lockVoteCard(teamId, true);
                } else if (response.status == 409 /* HttpStatusCode.Conflict "You have already rated!" */){
                    lockVoteCard(teamId, false);
                } else {
                    response.text().then(text => alert(text));
                }
            });
        });
    });
}