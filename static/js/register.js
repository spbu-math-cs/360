$(document).ready(function() {
    $('#register-form').submit(function(event) {
        event.preventDefault()
        var firstName = $("#first-name-input").val()
        var lastName = $("#last-name-input").val()
        var fatherName = $("#father-name-input").val()
        var login = $("#login-input").val()
        var password = $("#password-input").val()
        fetch('/register', {
             method: 'POST',
             headers: {
                 'Accept': 'application/json',
                 'Content-Type': 'application/json'
             },
             body: JSON.stringify(
                {
                    first_name: firstName,
                    last_name: lastName,
                    father_name: fatherName,
                    login: login,
                    password: password,
                    rank: 'second_grade'
                }
             )
         })
        .then(response => {
            if (response.ok) {
                window.location.href = "/login";
            } else {
                response.text().then(text => alert(text))
            }
        })
    })
})
