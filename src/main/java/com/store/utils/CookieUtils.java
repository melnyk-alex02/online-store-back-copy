package com.store.utils;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;

public class CookieUtils {
    public static String extractCookie(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies == null ? new Cookie[0] : cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public static Cookie createAccessTokenCookie(String cookieValue) {
        Cookie cookie = new Cookie("access_token", cookieValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        return cookie;
    }

    public static Cookie createRefreshTokenCookie(String cookieValue) {
        Cookie cookie = new Cookie("refresh_token", cookieValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge(60 * 24 * 60 * 60); //60 days
        return cookie;
    }

    public static Cookie[] clearCookies() {
        Cookie accessTokenCookie = new Cookie("access_token", "");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refresh_token", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        return new Cookie[]{accessTokenCookie, refreshTokenCookie};
    }
}