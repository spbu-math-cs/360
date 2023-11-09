$(document).ready(function() {
    $('#login-form').submit(function(event) {
        event.preventDefault()
        var login = $("#login-input").val()
        var password = $("#password-input").val()

        fetch('/login', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(
               {
                   login: login,
                   password: password
               }
            )
        })
        .then(response => {
            if (response.ok) {
                response.json().then(response => {
                    setCookie(TOKEN_COOKIE_NAME, response.token);
                    window.location.href = "/";
                })
            } else if (response.status == 409) {
                // login
                blinkWrongInput("#login-input");
            } else if (response.status == 400){
                // password
                blinkWrongInput("#password-input");
            } else {
                response.text().then(text => alert(text))
            }
        })
    })
})

async function blinkWrongInput(input) {
    $(input).addClass('invalid-blink');
    await new Promise(r => setTimeout(r, 2000));
    $(input).removeClass('invalid-blink');
}