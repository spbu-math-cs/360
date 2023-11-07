function logOut() {
    eraseCookie(TOKEN_COOKIE_NAME);
    window.location.href = "/";
}