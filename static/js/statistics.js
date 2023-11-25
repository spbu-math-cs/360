$(function() {
    var curr_url = window.location.href;
    var url = new URL(curr_url);
    var eventId = url.searchParams.get("eventId");
    if (eventId == null) {
        window.alert("You haven't choose demo to view statistics!");
        window.location.href = "/demo";
        return;
    }

    fetchStatictics(eventId);
    // var stats = {
    //     level: 0.4,
    //     presentation: 0.5,
    //     grade: 0.8,
    //     additional: 0.9,
    //     comments: ["комментарий", 
    //                "мой еомменкптвоповапыапыоапыаповыапв", 
    //                "Ребята хорошо постарались, респект!", 
    //                "Я КОЛЯ <script>while(true){alert(1)}</script>",
    //                "Мой очень длинный комментарий Мой очень длинный комментарий Мой очень длинный комментарийМой очень длинный комментарийМой очень длинный комментарийМой очень длинный комментарийМой очень длинный комментарийМой очень длинный комментарийМой "]
    // };
    // setStatistics(stats, 3);
});

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

function setStatistics(statistics, eventId) {
    $(`.statistics-content > h1`).html(`Demo ${eventId}`);
    
    $(`#ratio-1`).attr("style", `--ratio: ${statistics["level"]}`);
    $(`#ratio-1 > p`).html(Math.floor(statistics["level"] * 10) / 10);
    $(`#ratio-2`).attr("style", `--ratio: ${statistics["grade"]}`);
    $(`#ratio-2 > p`).html(Math.floor(statistics["grade"] * 10) / 10);
    $(`#ratio-3`).attr("style", `--ratio: ${statistics["presentation"]}`);
    $(`#ratio-3 > p`).html(Math.floor(statistics["presentation"] * 10) / 10);
    $(`#ratio-4`).attr("style", `--ratio: ${statistics["additional"]}`);
    $(`#ratio-4 > p`).html(Math.floor(statistics["additional"] * 10) / 10);

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
            <h3>${htmlEncode(comment["firstName"])} ${htmlEncode(comment["secondName"])} ${htmlEncode(comment["lastName"])}</h3>
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