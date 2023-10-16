$(document).ready(function() {
    $('#registerForm').submit(function(event) {
        var firstName = $("#firstNameInput").val();
        var lastName = $("#lastNameInput").val();
        var fatherName = $("#fatherNameInput").val();
        var login = $("#loginInput").val();
        var password = $("#passwordInput").val();
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
        .then(response => response.json())
        .then(response => console.log(JSON.stringify(response)))
        // TODO: fix and tell if error occured
        window.location.href = "/";
    });
});