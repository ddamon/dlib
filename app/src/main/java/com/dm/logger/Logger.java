package com.dm.logger;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dm.ConfigSetting;

/**
 * @Title: CommonLog.java
 * @Description: 日志输出类
 */
public class Logger {
	public static int logLevel = android.util.Log.VERBOSE; // 默认android.util.Log.VERBOSE;

	/**
	 * 
	 * @Description: 生成verbose级别的日志
	 * @param
	 * @return void
	 * @throws
	 */
	public static void v(String tag, Object str) {
		if (ConfigSetting.DEBUG) {
			verbose(tag, str);
		}
	}

	/**
	 * 
	 * @Description: 生成debug级别的日志
	 * @param
	 * @return void
	 * @throws
	 */
	public static void d(String tag, Object str) {
		if (ConfigSetting.DEBUG) {
			debug(tag, str);
		}
	}

	/**
	 * 
	 * @Description: 生成info级别的日志
	 * @param
	 * @return void
	 * @throws
	 */
	public static void i(String tag, Object obj) {
		if (ConfigSetting.DEBUG) {
			info(tag, obj);
		}
	}

	/**
	 * 
	 * @Description: 生成warn级别的日志
	 * @param
	 * @return void
	 * @throws
	 */
	public static void w(String tag, Object str) {
		if (ConfigSetting.DEBUG) {
			warn(tag, str);
		}
	}

	/**
	 * 
	 * @Description: 生成error级别的日志
	 * @param
	 * @return void
	 * @throws
	 */
	public static void e(String tag, Object str) {
		if (ConfigSetting.DEBUG) {
			error(tag, str);
		}
	}

	/**
	 * 
	 * @Description: 生成error级别的日志
	 * @param ex
	 *            -exception
	 * @return void
	 * @throws
	 */
	public static void e(String tag, Exception ex) {
		if (ConfigSetting.DEBUG) {
			error(tag, ex);
		}
	}

	private static void error(String tag, Object str) {
		if (logLevel <= Log.ERROR) {
			String name = getFunctionName();
			String ls = (name == null ? str.toString() : (name + " - " + str));
			Log.e(tag, ls);
		}
	}

	private static void debug(String tag, Object str) {
		if (logLevel <= Log.DEBUG) {
			String name = getFunctionName();
			String ls = (name == null ? str.toString() : (name + " - " + str));
			Log.d(tag, ls);
		}
	}

	/**
	 * 
	 * @Description: error level
	 * @param
	 * @return void
	 * @throws
	 */
	private static void error(String tag, Exception ex) {
		if (logLevel <= Log.ERROR) {
			StringBuffer sb = new StringBuffer();
			String name = getFunctionName();
			StackTraceElement[] sts = ex.getStackTrace();

			if (name != null) {
				sb.append(name + " - " + ex + "\r\n");
			} else {
				sb.append(ex + "\r\n");
			}

			if (sts != null && sts.length > 0) {
				for (StackTraceElement st : sts) {
					if (st != null) {
						sb.append("[ " + st.getFileName() + ":" + st.getLineNumber() + " ]\r\n");
					}
				}
			}

			Log.e(tag, sb.toString());
		}
	}

	/**
	 * 
	 * @Description: info level
	 * @param
	 * @return void
	 * @throws
	 */
	private static void info(String tag, Object obj) {
		if (logLevel <= Log.INFO) {
			String name = getFunctionName();
			String msg = name == null ? obj.toString() : (name + "-" + obj.toString());
			Log.i(tag, msg);
		}
	}

	/**
	 * 
	 * @Description: verbose level
	 * @param
	 * @return void
	 * @throws
	 */
	private static void verbose(String tag, Object str) {
		if (logLevel <= Log.VERBOSE) {
			String name = getFunctionName();
			String ls = (name == null ? str.toString() : (name + " - " + str));
			Log.v(tag, ls);
		}
	}

	/**
	 * 
	 * @Description: warn level
	 * @param
	 * @return void
	 * @throws
	 */
	private static void warn(String tag, Object str) {
		if (logLevel <= Log.WARN) {
			String name = getFunctionName();
			String ls = (name == null ? str.toString() : (name + " - " + str));
			Log.w(tag, ls);
		}
	}

	private static String getFunctionName() {
		// 获得当前堆栈中的信息元素集合
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (StackTraceElement st : elements) {
			if (st.isNativeMethod()) {
				continue;
			} else if (st.getClassName().equals(Thread.class.getName())) {// 剔除线程类
				continue;
			} else if (st.getClassName().equals(Logger.class.getName())) {// 剔除当前类
				continue;
			}
			return "[" + Thread.currentThread().getId() + " : " + st.getFileName() + " : " + st.getLineNumber() + "]";

		}
		return null;
	}

	/**
	 * 输出json
	 * 
	 * @param msg
	 * @return
	 */
	public static String toJson(Object msg) {
		String json = JSON.toJSONString(msg);
		if (json.length() > 500) {
			json = json.substring(0, 500);
		}
		return json;
	}
}
