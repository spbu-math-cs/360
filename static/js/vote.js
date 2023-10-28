$(document).ready(function() {
    let teams = [1, 2, 3, 4, 5, 6, 7];
    teams.forEach((teamId) => {
        document.getElementById("vote-cards").appendChild(
            htmlToElement(
            `
            <div id="vote-card-team${teamId}" class="col border rounded text-center justify-content-between p-3">
                <h3 class="my-2">Team ${teamId}</h3>
                <label for="grade-input-1" class="form-label mt-2">Сложность спринта</label>
                <input type="range" class="form-range" id="grade-input-team${teamId}-1" min="1" max="5">
                <label for="grade-input-2" class="form-label mt-2">Уровень выполнения</label>
                <input type="range" class="form-range" id="grade-input-team${teamId}-2" min="1" max="5">
                <label for="grade-input-3" class="form-label mt-2">Качество презентации</label>
                <input type="range" class="form-range" id="grade-input-team${teamId}-3" min="1" max="5">
                <label for="grade-input-4" class="form-label mt-2">Дополнительные баллы</label>
                <input type="range" class="form-range w-50" id="grade-input-team${teamId}-4" min="0" max="3" value="0">
                <input class="btn btn-outline-primary w-75 py-2 my-3" type="submit" value="Vote">
            </div>
            `
            )
        );
        $(`#vote-card-team${teamId}`).submit(function(event) {
            event.preventDefault()
            var grade_1 = $(`#grade-input-team${teamId}-1`).val()
            var grade_2 = $(`#grade-input-team${teamId}-2`).val()
            var grade_3 = $(`#grade-input-team${teamId}-3`).val()
            var grade_4 = $(`#grade-input-team${teamId}-4`).val()
            
            // TODO: change request template for not average, but different grades
            var grade_avg = 10 * (grade_1 + grade_2 + grade_3 + grade_4) / (5 + 5 + 5 + 3);

            fetch('/demo/vote', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(
                    {
                        grade: grade_avg,
                        password: password
                    }
                )
            })
            .then(response => {
                if (response.ok) {
                    response.json().then(_ => {
                        lock_vote_card(teamId);
                    })
                } else {
                    response.text().then(text => alert(text));
                }
            });
        });
    });
})

function lock_vote_card(teamId) {
    $(`#vote-card-team${teamId} > input`).each(function() {
        this.attr('disabled', 'disabled');
    });
}