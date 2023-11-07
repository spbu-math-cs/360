$(function() {
    if ($('body').hasClass('with-nav-bar')) {

        var logged = isLoggedIn();

        var selected = "active";
        var selectedClassDict = {
            "home" : "",
            "demo" : "",
            "about" : "",
            "profile" : ""
        }

        switch (getPageName()) {
            case "home":
                selectedClassDict["home"] = selected;
                break;
            case "demo":
            case "voting":
                selectedClassDict["demo"] = selected;
                break;
            case "about":
                selectedClassDict["about"] = selected;
                break;
            case "profile":
                selectedClassDict["profile"] = selected;
                break;
        }

        // Load navigation bar
        $('nav').html(
        `
        <div class="nav-buttons">
            <a id="home-navbar-button" class="${selectedClassDict["home"]}" href="/">Home</a>

            ${
                logged ? `
                    <a id="demo-navbar-button" class="${selectedClassDict["demo"]}" href="/demo">Demo</a>
                ` : ""
            }
            
            <a id="about-navbar-button" class="${selectedClassDict["about"]}" href="/about">About</a>
            <div class="nav-split">.</div>

            ${
                logged ? `
                    <a id="profile-navbar-button" class="${selectedClassDict["profile"]}" href="/profile">Profile</a>
                ` : `
                    <a id="login-navbar-button" href="/login">Log In</a>
                    <a id="register-navbar-button" href="/register">Register</a>
                `
            }

        </div>
        `);
    }
});