$(function() {
    let pageName = getPageName();
    var redirectsDict = isLoggedIn() ? {
        "login" : "/profile",
        "register" : "/profile"
    } : {
        "profile" : "/login",
        "demo" : "/login",
        "voting" : "/login"
    };
    if (pageName in redirectsDict) {
        window.location.href = redirectsDict[pageName];
    }
});

function getPageName() {
    let bodyClass = $('body').attr('class').match('body-[^\\s-]*-page')[0];
    return bodyClass.split('-')[1];
}