package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * hex/byte util
 * @author xuxueli 2015-11-14 22:47:28
 */
public class ByteHexConverter {
	private static Logger logger = LoggerFactory.getLogger(ByteHexConverter.class);

	/**
	 * byte - to - radix, use BigInteger
	 */
	private static final String hex_tables = "0123456789ABCDEF";
	public static String byte2hex (byte[] iBytes) {
		StringBuilder hex = new StringBuilder(iBytes.length * 2);
		for (int index = 0; index < iBytes.length; index++) {
			hex.append(hex_tables.charAt((iBytes[index] & 0xf0) >> 4));
			hex.append(hex_tables.charAt((iBytes[index] & 0x0f) >> 0));
		}		
		return hex.toString();
	}
	public static byte[] hex2Byte(String hexString) {
		if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }
		byte[] res = new byte[hexString.length() / 2];
		char[] chs = hexString.toCharArray();
		for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
			res[c] = (byte) (Integer.parseInt(new String(chs, i, 2), 16));
		}
		return res;
	}
	
	/**
	 * byte - to - radix, use BigInteger
	 */
	public static final int HEX = 16;
	public static String byte2radix(byte[] iBytes, int radix){
		return new BigInteger(1, iBytes).toString(radix);
	}
	public static byte[] radix2byte(String val, int radix){
		return new BigInteger(val, radix).toByteArray();
	}

	/**
	 * get length of string
	 * @param str
	 * @return len of string byte
	 */
	public static int getByteLen(String str){
		if (str==null || str.length()==0) {
			return 0;
		}
		// because java base on unicode, and one china code's length is one, but it's cost 2 bytes.
		//int len = str.getBytes().length * 2;
		int len = 0;
		try {
			len = str.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			len = str.getBytes().length * 2;
		}

		if (len % 4 != 0) {
			// Length is best in multiples of four
			len = (len/4 + 1) * 4;
		}
		return len;
	}

	public static void main(String[] args) {
		// hex - byte[] 方案A：位移
		String temp = "1111111111113d1f3a51sd3f1a32sd1f32as1df2a13sd21f3a2s1df32a13sd2f123s2a3d13fa13sd9999999999";
		System.out.println("明文:" + new String(temp.getBytes()));
		System.out.println("编码:" + byte2hex(temp.getBytes()));
		System.out.println("解码:" + new String(hex2Byte(byte2hex(temp.getBytes()))));
		
		// hex - byte[] 方案B：BigInteger
		System.out.println("编码:" + byte2radix(temp.getBytes(), HEX));
		System.out.println("解码:" + new String(radix2byte(byte2radix(temp.getBytes(), HEX), HEX)));

	}
	
}