function fillPage(containerId, userInfo, eventId, setComments, teams, digits) {
    var rank = userInfo["rank"];
    if (rank == "teacher") {
        $(containerId).append(`
<div class="graphs-container">
    <div class="graph-inner">
        <h3>Сложность спринта</h3>
        <div class="grades-graph" id="graph-1"></div>
    </div>
    <div class="graph-inner">
        <h3>Уровень выполнения</h3>
        <div class="grades-graph" id="graph-2"></div>
    </div>
    <div class="graph-inner">
        <h3>Качество презентации</h3>
        <div class="grades-graph" id="graph-3"></div>
    </div>
    <div class="graph-inner">
        <h3>Дополнительные баллы</h3>
        <div class="grades-graph" id="graph-4"></div>
    </div>
    <div class="graph-inner centered">
        <h3>Итоговый балл</h3>
        <div class="grades-graph" id="graph-5"></div>
    </div>
</div>
        `);
        createGraphs(teams);
        updateAllStatistics(eventId, teams, digits);
    } else {
        $(containerId).append(`
<div class="grades-container">
    <div class="ratio-cell">
        <div id="ratio-1" class="ratio">
        <p>0</p>
        </div>
        <p>Сложность спринта</p>
    </div>
    <div class="ratio-cell">
        <div id="ratio-2" class="ratio">
        <p>0</p>
        </div>
        <p>Уровень выполнения</p>
    </div>
    <div class="ratio-cell">
        <div id="ratio-3" class="ratio">
        <p>0</p>
        </div>
        <p>Качество презентации</p>
    </div>
    <div class="ratio-cell">
        <div id="ratio-4" class="ratio">
        <p>0</p>
        </div>
        <p>Доп. баллы</p>
    </div>
</div>
        `);
        if (setComments) {
            $(containerId).append(`
<h2>Comments</h2>
<div id="comments-container"></div>
            `);
        }
        fetchStatistics(eventId, setComments);
    }
}

function createGraphs(teams) {
    teams.forEach(team => {
        var teamId = team["teamId"];
        for (let i = 1; i < 6; i++) {
            $(`#graph-${i}`).append(`
<div class="item" id="graph-${i}-team${teamId}" style="--val: 0; --clr: #ffffff;">
    <div class="label">${teamId}</div>
    <div class="value"></div>
</div>
            `);   
        }
    });
}

function updateAllStatistics(eventId, teams, digits) {
    fetch(`/demo/statistics/average/all?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(statistics => {
                teams.forEach(team => {
                    var teamId = team["teamId"];
                    if (teamId in statistics) {
                        var grade = statistics[teamId];
                    } else {
                        var grade = {avgLevel: 0, avgGrade: 0, avgPresentation: 0, avgAdditional: 0};
                    }
                    const criteria = ["avgLevel", "avgGrade", "avgPresentation", "avgAdditional"];
                    for (let i = 1; i < 5; i++) {
                        var criterion = criteria[i - 1];
                        var minValue = (criterion == "avgAdditional") ? 0 : 1;
                        var maxValue = (criterion == "avgAdditional") ? 3 : 5;
                        setGraphColumn(`#graph-${i}-team${teamId}`, grade[criterion], minValue, maxValue, roundStr(grade[criterion], digits));
                    }
                    var totalGrade = calcGrade(grade[criteria[0]], grade[criteria[1]], grade[criteria[2]], grade[criteria[3]]);
                    setGraphColumn(`#graph-5-team${teamId}`, totalGrade, 0, 20, roundStr(totalGrade, digits));
                });
            });
        }
    });
}

function fetchStatistics(eventId, setComments) {
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
                setStatistics(responseJson, eventId, setComments);
            })
        } else {
            response.text().then(text => function() {
                alert("There's no statistics for this demo.");
                window.location.href = "/demo";
            });
        }
    });
}

function setStatistics(statistics, eventId, setComments) {
    $(`.statistics-content > h1`).html(`Demo ${eventId}`);
    
    $(`#ratio-1`).attr("style", `--ratio: ${normalize(statistics["avgLevel"], 1, 5)}`);
    $(`#ratio-1 > p`).html(roundStr(statistics["avgLevel"], 1));
    $(`#ratio-2`).attr("style", `--ratio: ${normalize(statistics["avgGrade"], 1, 5)}`);
    $(`#ratio-2 > p`).html(roundStr(statistics["avgGrade"], 1));
    $(`#ratio-3`).attr("style", `--ratio: ${normalize(statistics["avgPresentation"], 1, 5)}`);
    $(`#ratio-3 > p`).html(roundStr(statistics["avgPresentation"], 1));
    $(`#ratio-4`).attr("style", `--ratio: ${normalize(statistics["avgAdditional"], 0, 3)}`);
    $(`#ratio-4 > p`).html(roundStr(statistics["avgAdditional"], 1));

    if (setComments) {
        fetchComments(eventId);
    }
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