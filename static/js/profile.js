$(function() {
    $("#team-card").hide();
    $("#graph-card").hide();
    fetchInvitations();
    fetchTeamInfo();
    fetchPersonalGrades();
});

function fetchPersonalGrades() {
    fetch(`/profile/statistics`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            response.json().then(personalStatistics => {
                var prev = personalStatistics[0]["second"];
                var prev_id = personalStatistics[0]["first"];
                var maxGrade = 0;
                personalStatistics.forEach(eventGrade => {
                    maxGrade = eventGrade["second"] > maxGrade ? eventGrade["second"] : maxGrade;
                });
                maxGrade *= 1.1;
                var avg = prev;
                personalStatistics.slice(1).forEach(eventGrade => {
                    var curr = eventGrade["second"];
                    avg += curr;
                    $(`#personal-graph`).append(`
                        <tr>
                            <th scope="row">${prev_id}</th>
                            <td style="--start: ${prev / maxGrade}; --end: ${curr / maxGrade};"></td>
                        </tr>
                    `);
                    prev = curr;
                    prev_id = eventGrade["first"];
                });
                $(`#personal-graph`).children().last().append(`
<th scope="row" style="
    margin-left: auto;
">${prev_id}</th>
                `);
                $(`#average-grade-label`).html(Math.round(100 * avg / personalStatistics.length) / 100);
            });
        }
    });
}

function showError(message) {
    $(`#error-label`).html(message);
}

function hideError() {
    showError("");
}

function submitAvatar() {
    var files = document.getElementById("image-input").files;
    if (files.length == 0) {
        showError(`You haven't choosed an image`);
        return;
    }
    var file = files[0];
    let formData = new FormData();
    formData.append("blob", file);
    fetch('/profile/image/load', {
        method: 'POST',
        body: formData
    }).then(() => {
        window.location = "/profile";
    });
}

function validateImage() {
    var file = document.getElementById("image-input").files[0];
    hideError();
    if (file.type != "image/png" && file.type != "image/jpg" && file.type != "image/jpeg" && file.type != "image/webp") {
        if (!file.type.startsWith('image/')) {
            showError("Choosed file is not an image");
        } else {
            showError(`Choosed image is in unsupported format (supported formats: jpg, png, webp) `);
        }
    } else if (file.size > 300000) {
        showError(`Choosed file is too big (${Math.ceil(file.size / 1000)}kB > 300kB)`);
    } else return
    resetInput(`#change-avatar-popup`);
}

function fetchInvitations() {
    fetch('/profile/team/invite', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(async (response) => {
        if (response.ok) {
            setInvitations(await response.json());
        } else {
            $("#invitations-card").append(
                `<p>No invitations</p>`
            );
        }
    });
}

function setInvitations(invitations) {
    console.log(invitations);
    invitations.forEach(invitation => {
        $("#invitations-card").append(
        `
        <div id="invitation-${invitation["inviteId"]}" class="invitation">
            <h3>Team ${invitation["teamNum"]}</h3>
            <p>by ${invitation["inviterFirstName"]} ${invitation["inviterLastName"]}</p>
            <button class="round-button accept-button" onclick="answerInvitation(${invitation["inviteId"]}, 1)" type="button">✓</button>
            <button class="round-button decline-button" onclick="answerInvitation(${invitation["inviteId"]}, 0)" type="button">⤫</button>
        </div>    
        `
        )
    });
}

function fetchTeamInfo() {
    fetch('/profile/team/info', {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
    .then(async (response) => {
        if (response.ok) {
            $("#invitations-card").hide();
            $("#team-card").show();
            $("#graph-card").show();
            setTeamInfo(await response.json());
        }
    });
}

function setTeamInfo(team) {
    var teamId = team["teamId"];
    $("#team-id").html(`Team ${teamId}`);
    team["members"].forEach(member => {
        $("#team-members").append(
            `<li>${member["last_name"]} ${member["first_name"]} #${member["user_id"].toString().padStart(4, '0')}</li>`
        );
    });
}

function answerInvitation(inviteId, action) {
    fetch('/profile/team/invite/answer', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(
            {
                inviteId: inviteId,
                action: action
            }
        )
    })
    .then(response => {
        if (response.ok) {
            if (action == 1) {
                location.reload();
            } else {
                $(`#invitation-${inviteId}`).remove();
            }
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function logOut() {
    eraseCookie(TOKEN_COOKIE_NAME);
    window.location.href = "/";
}

function openPopup(popupId) {
    $(`${popupId}`).addClass("active");
    $(`${popupId} input`)[0].focus();
}

function closePopup(popupId) {
    $(`${popupId}`).removeClass("active");
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

    $("#id-input").on("keypress", function(e) {
        if (e.key === "Enter") {
            e.preventDefault();
            // Trigger the button element with a click
           $("#send-invite-button").click();
          }
    });
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

function resetInput(popupId) {
    $(`${popupId} input`).val("");
    $(`${popupId} .search-bar`).removeClass("complete");
}

function inviteMember() {
    var UID = $("#id-input").val();
    if ($(".search-bar").hasClass("complete")) {
        var inputId = $("#id-input").val();
        console.log(inputId);
        fetch('/profile/team/invite/send', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(
                {
                    UID: UID
                }
            )
        })
        .then(response => {
            if (response.ok) {
                resetInput("#invite-popup");
                closePopup("#invite-popup");
            } else {
                response.text().then(text => alert(text));
            }
        });
    }
}

function leaveTeam() {
    fetch('/profile/team/leave', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: ""
    })
    .then(response => {
        if (response.ok) {
            location.reload();
        } else {
            response.text().then(text => alert(text));
        }
    });
}

function setPasswordMessage(message, status) {
    if (status) {
        $("#password-status").addClass("success");
    } else {
        $("#password-status").removeClass("success");
    }

    $("#password-status").html(message);
}

function changePassword() {
    console.log("123");
    var oldPassword = $("#old-password").val();
    var newPassword = $("#new-password").val();
    if (oldPassword == newPassword) {
        setPasswordMessage("The new password cannot be the same as the old password", false);
        return;
    }
    fetch('/profile/change/password', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(
            {
                oldPassword: oldPassword,
                newPassword: newPassword
            }
        )
    })
    .then(response => {
        if (response.ok) {
            setPasswordMessage("You have successfully changed your password", true);
        } else if (response.status == 400) {
            setPasswordMessage("Wrong password!", false);
        } else {
            response.text().then(text => alert(text));
        }
    });
}