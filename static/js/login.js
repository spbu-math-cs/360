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
            } else {
                response.text().then(text => alert(text))
            }
        })
    })
})
