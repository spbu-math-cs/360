function logOut() {
    eraseCookie(TOKEN_COOKIE_NAME);
    redirectTo("/");
}