async function spin(object) {
    if (!$(object).hasClass("active")) {
        $(object).addClass("active");
        await new Promise(resolve => setTimeout(resolve, 1000));
        $(object).removeClass("active");
    }
}

$(function() {
    spin("#spinner");
    $("#spinner").on("mouseover", function() {
        spin(this);
    });
});