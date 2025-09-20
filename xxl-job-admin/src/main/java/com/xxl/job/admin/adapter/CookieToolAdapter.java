package com.xxl.job.admin.adapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Ice2Faith
 * @date 2025/9/19 18:57
 */
public class CookieToolAdapter {
    private static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
    private static final String COOKIE_PATH = "/";

    public CookieToolAdapter() {
    }

    private static void set(HttpServletResponse response, String key, String value, String domain, String path, int maxAge, boolean isHttpOnly) {
        try {
            value = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException var8) {
            throw new RuntimeException();
        }

        Cookie cookie = new Cookie(key, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }

        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }

    public static void set(HttpServletResponse response, String key, String value, boolean ifRemember) {
        int age = ifRemember ? Integer.MAX_VALUE : -1;
        set(response, key, value, (String)null, "/", age, true);
    }

    public static void set(HttpServletResponse response, String key, String value, int maxAge) {
        set(response, key, value, (String)null, "/", maxAge, true);
    }

    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            set(response, key, "", (String)null, "/", 0, true);
        }

    }

    private static Cookie get(HttpServletRequest request, String key) {
        Cookie[] arr_cookie = request.getCookies();
        if (arr_cookie != null && arr_cookie.length > 0) {
            Cookie[] var3 = arr_cookie;
            int var4 = arr_cookie.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Cookie cookie = var3[var5];
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static String getValue(HttpServletRequest request, String key) {
        Cookie cookie = get(request, key);
        if (cookie == null) {
            return null;
        } else {
            String value = cookie.getValue();

            try {
                value = URLDecoder.decode(value, "utf-8");
                return value;
            } catch (UnsupportedEncodingException var5) {
                UnsupportedEncodingException e = var5;
                throw new RuntimeException(e);
            }
        }
    }
}
