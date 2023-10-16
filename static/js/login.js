$(document).ready(function() {
    $('#loginForm').submit(function(event) {
        var login = $("#loginInput").val();
        var password = $("#passwordInput").val();
        fetch('/login', {
             method: 'POST',
             headers: {
                 'Accept': 'application/json',
                 'Content-Type': 'application/json'
             },
             body: JSON.stringify( {login: login, password: password} )
         })
        .then(response => response.json())
        .then(response => console.log(JSON.stringify(response)))
        setCookie(TOKEN_COOKIE_NAME, "12345");
        window.location.href = "/";
    });
});