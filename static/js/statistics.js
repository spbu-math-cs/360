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

    // fetchRank(eventId);
    fillPage({rank: "teacher"}, eventId);
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
                fillPage(responseJson, eventId);
            })
        } else {
            response.text().then(text => function() { alert(text) });
        }
    });
}

function fillPage(userInfo, eventId) {
    var rank = userInfo["rank"];
    if (rank == "teacher") {
        $(`#statistics-content`).append(`
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
</div>
        `);
        fetchAllStatistics(eventId);
    } else {
        $(`#statistics-content`).append(`
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
<h2>Comments</h2>
<div id="comments-container"></div>
        `);
        fetchStatictics(eventId);
    }
}

function fetchAllStatistics(eventId) {
    fetch(`/demo/statistics/average/all?eventId=${eventId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            response.json().then(responseJson => {
                setAllStatistics(responseJson, eventId);
            })
        } else {
            response.text().then(text => function() {
                alert("There's no statistics for this demo.");
                window.location.href = "/demo";
            });
        }
    });
}

function setAllStatistics(statistics, eventId) {
    jQuery.each(statistics, function(teamId, grade) {
        const criteria = ["avgLevel", "avgGrade", "avgPresentation", "avgAdditional"];
        for (let i = 1; i < 5; i++) {
            var criterion = criteria[i - 1];
            if (criterion == "avgAdditional") {
                var grade_1 = normalize(grade[criterion], 0, 3);
            } else {
                var grade_1 = normalize(grade[criterion], 1, 5);
            }
            var grade_100 = Math.floor(grade_1 * 100);
            var color = calcColorBetween("92DF7E", "12486B", grade_1);
            $(`#graph-${i}`).append(`
    <div class="item" id="graph-${i}-team${teamId}" style="--val: ${grade_100}; --clr: #${color};">
        <div class="label">${teamId}</div>
        <div class="value">${Math.floor(grade[criterion] * 10) / 10}</div>
    </div>
            `);   
        }
    });
}

function fetchStatictics(eventId) {
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
                setStatistics(responseJson, eventId);
            })
        } else {
            response.text().then(text => function() {
                alert("There's no statistics for this demo.");
                window.location.href = "/demo";
            });
        }
    });
}


function normalize(value, lBound, rBound) {
    return Math.min(Math.max((value - lBound) / (rBound - lBound), 0), 1);
}

function setStatistics(statistics, eventId) {
    $(`.statistics-content > h1`).html(`Demo ${eventId}`);
    
    $(`#ratio-1`).attr("style", `--ratio: ${normalize(statistics["avgLevel"], 1, 5)}`);
    $(`#ratio-1 > p`).html(Math.floor(statistics["avgLevel"] * 10) / 10);
    $(`#ratio-2`).attr("style", `--ratio: ${normalize(statistics["avgGrade"], 1, 5)}`);
    $(`#ratio-2 > p`).html(Math.floor(statistics["avgGrade"] * 10) / 10);
    $(`#ratio-3`).attr("style", `--ratio: ${normalize(statistics["avgPresentation"], 1, 5)}`);
    $(`#ratio-3 > p`).html(Math.floor(statistics["avgPresentation"] * 10) / 10);
    $(`#ratio-4`).attr("style", `--ratio: ${normalize(statistics["avgAdditional"], 0, 3)}`);
    $(`#ratio-4 > p`).html(Math.floor(statistics["avgAdditional"] * 10) / 10);

    fetchComments(eventId);
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