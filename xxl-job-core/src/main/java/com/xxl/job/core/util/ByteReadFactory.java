package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * byte read util
 * @author xuxueli 2015-11-15 03:50:10
 */
public class ByteReadFactory {
	private static transient Logger logger = LoggerFactory.getLogger(ByteReadFactory.class);
	private int m_iPos;
	private int m_iReqLen;
	private byte[] m_byte = null;

	public ByteReadFactory(byte[] hexBytes){
		m_iPos = 0;
		m_byte = hexBytes;
		m_iReqLen = m_byte.length;
	}
	
	public int readInt() {
		if (m_iPos + 4 > m_iReqLen) {
			return 0;
		}
		int iInt = (m_byte[m_iPos] & 0xff) 
				| ((m_byte[m_iPos + 1] & 0xff) << 8)
				| ((m_byte[m_iPos + 2] & 0xff) << 16)
				| ((m_byte[m_iPos + 3] & 0xff) << 24);		
		m_iPos += 4;
		return iInt;
	}
	
	public long readLong() {
		if (m_iPos + 8 > m_iReqLen) {
			return 0;
		}
		long iLong = (m_byte[m_iPos] & 0xff) 
				| ((m_byte[m_iPos + 1] & 0xff) << 8)
				| ((m_byte[m_iPos + 2] & 0xff) << 16)
				| ((m_byte[m_iPos + 3] & 0xff) << 24)
				| ((m_byte[m_iPos + 4] & 0xff) << 32)
				| ((m_byte[m_iPos + 5] & 0xff) << 40)
				| ((m_byte[m_iPos + 6] & 0xff) << 48)
				| ((m_byte[m_iPos + 7] & 0xff) << 56);
		m_iPos += 8;
		return iLong;
	}
	
	public String readString(int length) {
		if (m_iPos + length > m_iReqLen) {
			logger.error("[byte stream factory read string length error.]");
			return "";
		}
		
		int index = 0;
		for (index = 0; index < length; index++) {
			if (m_byte[m_iPos + index] == 0) {
				break;
			}
		}
		String msg = "";
		try {
			msg = new String(m_byte, m_iPos, index, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("[byte stream factory read string exception.]", e);
		}
		m_iPos += length;
		
		return msg;
	}
	
	public byte[] read(int length) {
		if (m_iPos + length > m_iReqLen || length<=0) {
			logger.error("[byte stream factory read string length error.]");
			return null;
		}
		for (int i = 0; i < length; i++) {
			if (m_byte[m_iPos + i] == 0) {
				break;
			}
		}
		
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = m_byte[m_iPos + i];
		}
		m_iPos += length;
		return result;
	}
	
	public byte[] readByteAll() {
		return read(m_iReqLen - m_iPos);
	}
	
}
