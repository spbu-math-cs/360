function foldUnfoldContent(heading) {
    if ($(heading).hasClass("active")) {
        $(heading).removeClass("active");
        $(heading).parent().css("grid-template-rows", "min-content 0fr");
    } else {
        $(heading).addClass("active");
        $(heading).parent().css("grid-template-rows", "min-content 1fr");
    }
}