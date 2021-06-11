package com.xxl.job.admin.core.util;

/**
 *
 * @author AntzUhl 2021/6/11 4:48 下午
 **/
public class StringUtil {


    /**
     * Constant time for same length String comparison, to prevent timing attacks
     *
     * @param a
     *            The string a
     * @param b
     *            the string b
     * @return true is the 2 strings are equals
     */
    public static boolean safeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        } else {
            char equal = 0;
            for (int i = 0; i < a.length(); i++) {
                equal |= a.charAt(i) ^ b.charAt(i);
            }
            return equal == 0;
        }
    }

}
