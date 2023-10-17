$(document).ready(function() {
    $('#registerForm').submit(function(event) {
        event.preventDefault()
        var firstName = $("#firstNameInput").val()
        var lastName = $("#lastNameInput").val()
        var fatherName = $("#fatherNameInput").val()
        var login = $("#loginInput").val()
        var password = $("#passwordInput").val()
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
