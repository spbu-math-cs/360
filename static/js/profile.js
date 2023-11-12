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

$(function() {
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