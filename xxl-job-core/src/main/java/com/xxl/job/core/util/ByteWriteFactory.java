package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * byte write util
 * @author xuxueli 2015-11-15 03:49:36
 */
public class ByteWriteFactory {
	private static transient Logger logger = LoggerFactory.getLogger(ByteWriteFactory.class);
	private ByteBuffer m_byteBuf = null;	
	public ByteWriteFactory() {
		m_byteBuf = ByteBuffer.allocate(1024 * 4);
	}
	public ByteWriteFactory(int capacity) {
		m_byteBuf = ByteBuffer.allocate(capacity);
	}
	
	public void writeInt(int intValue) {
		byte[] intBytes = new byte[4];
		for (int index = 0; index < 4; index++) {
			intBytes[index] = (byte) (intValue >>> (index * 8));
		}
		m_byteBuf.put(intBytes);
	}
	
	public void write(int[] intArr) {
		for (int index = 0; index < intArr.length; index++) {
			writeInt(intArr[index]);
		}
	}
	
	public void write(byte[] byteArr) {
		m_byteBuf.put(byteArr);
	}
	
	public void writeString(String value, int length) {
		byte[] bytes = new byte[length];
		if (value != null && value.trim().length() > 0) {
			try {
				byte[] infoBytes = value.getBytes("UTF-8");
				int len = infoBytes.length < length ? infoBytes.length : length;			
				System.arraycopy(infoBytes, 0, bytes, 0, len);
			} catch (UnsupportedEncodingException e) {
				logger.error("[response stream factory encoding exception.]", e);
			}
		}	
		m_byteBuf.put(bytes);	
	}
	
	public byte[] getBytes() {
		m_byteBuf.flip();
		if (m_byteBuf.limit() == 0) {
			return null;
		}
		
		byte[] bytes = new byte[m_byteBuf.limit()];
		m_byteBuf.get(bytes);
		
		return bytes;
	}
	
}
