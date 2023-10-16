var loggedIn; // is client logged into server
const TOKEN_COOKIE_NAME = "access_token";

// Cookie functions

function setCookie(name,value,days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}
function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
function eraseCookie(name) {   
    // document.cookie = name+'=; Max-Age=-99999999;';  
    document.cookie = name+'=; Max-Age=0; path=/; domain=' + location.hostname;
}

// ---------------------

function showVisibilitySignIn() {
    document.getElementById("sign-in-button").style.display = 'inline';
}

function showProfileButton() {
    document.getElementById("profile-button").style.display = 'inline';
}

function showDemoMenuOption() {
    document.getElementById("menu-option-demo").style.display = 'inline';
}

function checkLoggedIn() {
    // if cliend is logged (he has token), hide sign-in button and show profile button
    // by default, sign-in button is showed, and profile is hidden
    var pageName = getPageName();
    if (pageName != "login" && pageName != "register") {
        if (getCookie(TOKEN_COOKIE_NAME) != null) {
            // user not logged in
            window.loggedIn = true;
            showProfileButton();
            showDemoMenuOption();
        } else {
            // user is not logged in
            window.loggedIn = false;
            showVisibilitySignIn();
        }
    }    
}
// --------------------------------------

function redirectTo(page) {
    window.location.href = page;
}

// --------------------------------------

function logOutSubmit() {
    eraseCookie(TOKEN_COOKIE_NAME);
    redirectTo("home");
}

// -----------------------------------------

function getPageName() {
    var sPath = window.location.pathname;
    return sPage = sPath.substring(sPath.lastIndexOf('/') + 1);
}

function redirectLogin() {
    var pageName = getPageName();
    if ((pageName == "demo" || pageName == "profile") && !window.loggedIn) {
        redirectTo("login");
    }
    if ((pageName == "login" || pageName == "register") && window.loggedIn) {
        redirectTo("home");
    }
}

window.onload = function() {
    checkLoggedIn();
    redirectLogin();
}
