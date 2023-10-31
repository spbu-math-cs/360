var loggedIn; // is client logged into server
const TOKEN_COOKIE_NAME = "token";

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

function showVisibilitySignUp() {
    document.getElementById("sign-up-button").style.display = 'inline';
}

function showProfileButton() {
    document.getElementById("profile-button").style.display = 'inline';
}

function showDemoMenuOption() {
    document.getElementById("menu-option-demo").style.display = 'block';
}

function checkLoggedIn() {
    // if cliend is logged (he has token), hide sign-in button and show profile button
    // by default, sign-in button is showed, and profile is hidden
    if (!onPage("login") && !onPage("register")) {
        if (getCookie(TOKEN_COOKIE_NAME) != null) {
            // user not logged in
            window.loggedIn = true;
            showProfileButton();
            showDemoMenuOption();
        } else {
            // user is not logged in
            window.loggedIn = false;
            showVisibilitySignIn();
            showVisibilitySignUp();
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
    redirectTo("/");
}

// -----------------------------------------

function getPageName() {
    var sPath = window.location.pathname;
    return sPage = sPath.substring(sPath.lastIndexOf('/') + 1);
}

function redirectLogin() {
    if ((onPage("demo") || onPage("profile")) && !window.loggedIn) {
        redirectTo("login");
    }
    if ((onPage("login") || onPage("register")) && window.loggedIn) {
        redirectTo("/");
    }
}

function htmlToElement(html) {
    var template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
}

function setDemosInPage(demosJson) {
    demosJson.forEach(demo => {
        let id = demo["eventId"];
        let date = demo["date"];
        let start = demo["start"];
        let finish = demo["finish"];
        let date_s = date.split('-');
        // console.log(date_s);
        let date_f = date_s[0] + '-' + date_s[1] + '-' + date_s[2];
        let st = Date.parse(date_f + 'T' + start + ":00.000Z");
        let fn = Date.parse(date_f + 'T' + finish + ":00.000Z");
        let now = Date.now();
        // console.log(st);
        // console.log(fn);
        // console.log(now);
        // console.log(date_f + 'T' + start + ":00.000Z");
        
        let demo_class = "locked-demo"
        if (st <= now && now <= fn) {
            demo_class = "";
        } else if (now > fn) {
            demo_class = "disabled-demo";
        }
        // window.alert(Date.parse(date + start));
        document.getElementById("demo-container").appendChild(
            htmlToElement(
            `
            <div class="col">
                <div class="card shadow-sm">
                    <a class="card-block stretched-link text-decoration-none demo-card ${demo_class}" href="/demo/grades?eventId=${id}">
                        <div class="card-body">
                            <h2 class="card-text text-center">Demo ${id}</h2>
                            <p class="text-body-secondary text-center">${date}</p>
                        </div>
                    </a>
                </div>
            </div>
            `
            )
        );
    });
}

function getDemos() {
    if (onPage("demo")) {
        fetch('/demo_list', {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(response => setDemosInPage(response));
    }
}

function onPage(pageName) {
    return window.location.href.match(pageName) != null;
}

window.onload = function() {
    checkLoggedIn();
    redirectLogin();
    getDemos();
}
