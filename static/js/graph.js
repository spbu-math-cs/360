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

function normalize(value, lBound, rBound) {
    return Math.min(Math.max((value - lBound) / (rBound - lBound), 0), 1);
}

function roundStr(x, digits) {
    if (digits == 0) return `${Math.floor(x)}`;
    return `${Math.floor(x * Math.pow(10, digits)) / Math.pow(10, digits)}`;
}

function setGraphColumn(columnId, value, minValue, maxValue, valueText) {
    var ratio = normalize(value, minValue, maxValue);
    var color = calcColorBetween("92DF7E", "12486B", ratio);
    $(columnId).attr("style", `--val: ${ratio * 100}; --clr: #${color};`);
    $(columnId).children(".value").html(valueText);
}

function calcGrade(grade_1, grade_2, grade_3, grade_4) {
    return (grade_1 + grade_2 + grade_3) * (1.0 + grade_4 / 9.0);
}

function calcGradeForTeam(teamId) {
    var grade_1 = parseInt($(`#grade-input-team${teamId}-1`).val());
    var grade_2 = parseInt($(`#grade-input-team${teamId}-2`).val());
    var grade_3 = parseInt($(`#grade-input-team${teamId}-3`).val());
    var grade_4 = parseInt($(`#grade-input-team${teamId}-4`).val());
    return calcGrade(grade_1, grade_2, grade_3, grade_4);
}

function updateGraph() {
    $(`.grades-graph > .item`).each(function() {
        var teamId = parseInt($(this).attr("id").substring(10));
        var grade = calcGradeForTeam(teamId);
        setGraphColumn(this, grade, 0, MAX_GRADE, roundStr(grade, 1));
    });
}