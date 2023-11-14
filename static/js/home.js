var direction = true;

$(function() {
    $(".joebiden").on("mousedown", function() {
        if (direction) {
            $(this).addClass("counterclockwise");
            $(this).removeClass("clockwise");
        } else {
            $(this).addClass("clockwise");
            $(this).removeClass("counterclockwise");
        }
        direction = !direction;
    });

    $(".joebiden").on("mouseup", function() {
        $(this).removeClass("counterclockwise");
        $(this).removeClass("clockwise");
    });

    $(".joebiden").on("click", function() {
        if ($(this).hasClass("zero")) {
            $(this).addClass("seven");
            $(this).removeClass("zero");
        } else {
            $(this).addClass("zero");
            $(this).removeClass("seven");
        }
    })
});