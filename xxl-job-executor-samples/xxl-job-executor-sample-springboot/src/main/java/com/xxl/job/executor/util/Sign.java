package com.xxl.job.executor.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.xxl.job.executor.core.config.URLConfig;

public class Sign {

	private static final String KEY_MD5 = "MD5";

	/**
	 * 32位md5加密
	 * 
	 * @param str
	 */
	public static String get32MD5(String str) {
		str = URLConfig.SIGN_KEY + str;
		StringBuilder buf = new StringBuilder("");
		try {
			MessageDigest md = MessageDigest.getInstance(KEY_MD5);
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();// 32位的加密
	}

}
