package com.xxl.job.core.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.xxl.job.core.context.XxlJobHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * operate process
 * based on JNA
 * @author season8
 */
public class ProcessUtil {
	private static final Logger log = LoggerFactory.getLogger(ProcessUtil.class);

	private static final String FORCED_KILL_COMMAND_WIN = "taskkill /F /PID ";
	private static final String FORCED_KILL_COMMAND_LINUX = "kill -9 ";

	/**
	 * win library
	 */
	interface Kernel32 extends Library {
		Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

		/**
		 * get PID by handle
		 * overload method, should not change
		 *
		 */
		int GetProcessId(Long handle);
	}

	/**
	 * destroy process forcible
	 */
	public static boolean destroyProcess(Process process) {
		if (!process.isAlive()) {
			return true;
		}
		int pid = getPid(process);

		if (pid == 0) {
			return false;
		}

		if (!process.isAlive()) {
			return true;
		}
		try {
			String command = Platform.isWindows() ? FORCED_KILL_COMMAND_WIN : FORCED_KILL_COMMAND_LINUX;
			Runtime.getRuntime().exec(command + pid);
			return true;
		} catch (IOException e) {
			XxlJobHelper.log(e);
			return false;
		}
	}

	/**
	 * get PID of process
	 */
	public static int getPid(Process process) {
		try {
			if (Platform.isWindows()) {
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				return Kernel32.INSTANCE.GetProcessId((Long) f.get(process));
			} else if (Platform.isLinux()) {
				Field f = process.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				return (int) (Integer) f.get(process);
			}
		} catch (Exception e) {
			XxlJobHelper.log(e);
		}
		return 0;
	}
}