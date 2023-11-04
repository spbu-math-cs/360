$(function() {
    if ($('body').hasClass('with-nav-bar')) {

        var visibleOptionClass = "visible-block";
        var visibleButtonClass = "visible-inline";
        var hiddenClass = "hidden";

        var visibilityClassDict = isLoggedIn() ? {
            "demo" : visibleOptionClass,
            "login" : hiddenClass,
            "register" : hiddenClass,
            "profile" : visibleButtonClass
        } : {
            "demo" : hiddenClass,
            "login" : visibleButtonClass,
            "register" : visibleButtonClass,
            "profile" : hiddenClass
        };


        var selected = "link-secondary";
        var selectedClassDict = {
            "home" : "",
            "demo" : "",
            "about" : ""
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
        }

        // Load navigation bar
        $('nav').html(
        `
        <div class="container">
            <header
                class="d-flex flex-wrap align-items-center justify-content-center justify-content-md-between py-3 mb-4 border-bottom">
                <div class="col-md-3 mb-2 mb-md-0"></div>

                <ul class="nav col-12 col-md-auto mb-2 justify-content-center mb-md-0">
                    <li><a id="menu-option-home" href="/" class="${selectedClassDict["home"]} nav-link px-2">Home</a></li>
                    <li><a id="menu-option-demo" href="/demo" class="${selectedClassDict["demo"]} ${visibilityClassDict["demo"]} nav-link px-2">Demo</a></li>
                    <li><a id="menu-option-about" href="/about" class="${selectedClassDict["about"]} nav-link px-2">About</a></li>
                </ul>

                <div class="col-md-3 text-end">
                    <a id="sign-in-button" href="/login" class="${visibilityClassDict["login"]} btn btn-outline-primary me-2">Sign In</a>
                    <a id="sign-up-button" href="/register" class="${visibilityClassDict["register"]} btn btn-primary me-2">Sign Up</a>
                    <a id="profile-button" href="/profile" class="${visibilityClassDict["profile"]} btn btn-outline-primary me-2">My Profile</a>
                </div>
            </header>
        </div>
        `);
    }
});