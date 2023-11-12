function logOut() {
    eraseCookie(TOKEN_COOKIE_NAME);
    window.location.href = "/";
}

function openPopup() {
    $(".popup").addClass("active");
}

function closePopup() {
    $(".popup").removeClass("active");
}

function searchUser() {
    var id = $("#id-input").val();
    // TODO: ... GET request ...
    var firstName = "Василий";
    var lastName = "Можаев";
    var fatherName = "Михайлович";
    var id = "0002";
    $("#search-result-last-name").html(lastName);
    $("#search-result-first-name").html(firstName);
    $("#search-result-father-name").html(fatherName);
    $("#search-result-id").html("#" + id);
    $("#search-result").children().css("visibility", "visible");          
}

function resetSearch() {
    $("#search-result-last-name").html('.');
    $("#search-result-first-name").html('.');
    $("#search-result-father-name").html('.');
    $("#search-result-id").html('.');
    $("#search-result").children().css("visibility", "hidden");
}

$(function() {
    $("#id-input").keypress(function(event) {
        if(event.which == 13) {
            searchUser();
        }
    });

    $("#id-input").inputFilter(function(value) {
        return /^\d{0,4}$/.test(value);    // Allow digits only, using a RegExp
    }, "Only digits allowed");

    $("#id-input").on("input", function(e) {
        if (this.value.length == 4) {
            $(".search-bar").addClass("complete");
        } else {
            $(".search-bar").removeClass("complete");
        }
    })
});

// Restricts input for the set of matched elements to the given inputFilter function.
// Source: https://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery/995193#995193
(function($) {
    $.fn.inputFilter = function(callback) {
        return this.on("input keydown keyup mousedown mouseup select contextmenu drop focusout", function(e) {
            if (callback(this.value)) {
                // Accepted value
                if (["keydown","mousedown","focusout"].indexOf(e.type) >= 0){
                    $(this).removeClass("input-error");
                    this.setCustomValidity("");
                }
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;

            } else if (this.hasOwnProperty("oldValue")) {
                // Rejected value - restore the previous one
                $(this).addClass("input-error");
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                // Rejected value - nothing to restore
                this.value = "";
            }
        });
    };
}(jQuery));