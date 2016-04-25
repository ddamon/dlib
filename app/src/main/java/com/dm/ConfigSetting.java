package com.dm;

import com.dm.utils.Perference;

/**
 * 项目初始配置 放到pre中
 */
public class ConfigSetting extends Perference {
	/* 是否debug */
	public static boolean DEBUG = true;
	// 键盘高度
	public int KEYBOARD_HEIGHT = 400;
	/* 数据库版本 */
	public static int DATABASE_VERSION = 1;
	public static String ROOT_PATH = "root_path";
	/* 崩溃重启 */
	public static boolean CRASH_TO_RESTART = true;
	/* 崩溃日志记录 */
	public static boolean CRASH_LOG_ENABLE = true;
	public static String CRASH_LOG_PATH = "crash_log";

	// 业务数据sdcard缓存路径，相对root_path路径
	public String COM_M_COMMON_JSON = "com_m_common_json";
	// 图片数据sdcard缓存路径，相对root_path路径
	public String COM_M_COMMON_IMAGE = "com_m_common_image";

	// 服务地址
	public String BASE_URL = "base_url";
	// 缓存开关
	public boolean CACHE_ENABLE = false;
	// 缓存类型
	public String CACHE_UTILITY = "cache_utility";
	// 内存缓存
	public String MEMORY_CACHE_UTILITY = "memory_cache_utility";
	// 内存缓存有效时间
	public int MEMORY_CACHE_VALIDITY = 300;

	// 分页大小
	public int PAGE_COUNT = 10;

}