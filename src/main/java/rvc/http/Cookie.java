package rvc.http;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {

    public static Map<String, String> cookies() {
        return Stream.of(Request.get().servletRequest.getCookies())
                .collect(
                        Collectors.toMap(
                                javax.servlet.http.Cookie::getName,
                                javax.servlet.http.Cookie::getValue
                        )
                );
    }

    public static void cookie(String name, String value) {
        cookie(name, value, -1, false);
    }

    public static void cookie(String name, String value, int maxAge) {
        cookie(name, value, maxAge, false);
    }

    public static void cookie(String name, String value, int maxAge, boolean secured) {
        cookie(name, value, maxAge, secured, false);
    }

    public static void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        cookie("", name, value, maxAge, secured, httpOnly);
    }

    public static void cookie(String path, String name, String value, int maxAge, boolean secured) {
        cookie(path, name, value, maxAge, secured, false);
    }

    public static void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secured);
        cookie.setHttpOnly(httpOnly);
        Response.get().raw().addCookie(cookie);
    }

    public static void removeCookie(String name) {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, "");
        cookie.setMaxAge(0);
        Response.get().response.addCookie(cookie);
    }
}
