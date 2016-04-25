package com.dm;

public class Consts {

	public static final boolean auto_inject = true;
	/* 这个值被设为true,在doInBackground和doInUI中发生的错误都会被try-catch掉 */
	public static boolean net_error_try = false;
	// adapter 的分页相关
	public static String netadapter_page_no = "current_page";
	public static String netadapter_step = "step";
	public static Integer netadapter_step_default = 20;
	public static String netadapter_timeline = "timeline";
	public static String netadapter_json_timeline = "id";
	public static String netadapter_no_more = "已经没有了";
	// 依赖注入的包
	public static String[] ioc_instal_pkg = null;

	// 图片缓存相关
	public static String image_cache_dir = "dhcache";
	public static int image_cache_num = 12;
	public static Boolean image_cache_is_in_sd = false;
	public static long image_cache_time = 100000L;

	// 网络访问返回数据的格式定义
	public static String response_success = "success";
	public static String response_msg = "msg";
	public static String response_data = "data";
	public static String response_code = "code";
	public static String response_status = "status";
	public static String response_version = "version";
	public static String response_validation = "validation";
	// 线程池大小
	public static int net_pool_size = 10;

	private Consts() {
	}
}