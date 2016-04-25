package com.dm.utils;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class PackageUtil {
	/**
	 * App installation location flags of android system
	 */
	public static final int APP_INSTALL_AUTO = 0;
	public static final int APP_INSTALL_INTERNAL = 1;
	public static final int APP_INSTALL_EXTERNAL = 2;

	public static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
	public static int UNINSTALLED = 1; // 表示未安装
	public static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低

	/**
	 * 调用系统安装应用
	 */
	public static boolean install(Context context, File file) {
		if (file == null || !file.exists() || !file.isFile()) {
			return false;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		return true;
	}

	/**
	 * 
	 * @param context
	 * @param file
	 * @param type
	 *            根据哪个字段判断状态
	 * @return
	 */
	public static Intent intsallOrOpenApkIntent(Context context, File file, int type, boolean isNeedStartApp) {
		Intent intent = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(file.getPath(), PackageManager.GET_ACTIVITIES);
		// ActivityInfo ai = packageInfo.activities[0];
		/** 得到包名 */
		String packageName = packageInfo.packageName;
		/** apk的版本号码 int */
		int versionCode = packageInfo.versionCode;
		String versionName = packageInfo.versionName;
		/** 安装处理类型 */
		int apkState = getAPKState(pm, packageName, versionCode, versionName, type);
		if (apkState == 0) {// 已经安装且版本相同
			if (isNeedStartApp) {
				intent = pm.getLaunchIntentForPackage(packageName);// 根据包名启动
			}
		} else {
			intent = getApkFileIntent(file);// 打开apk文件进行安装
		}
		return intent;
	}

	/**
	 * 调用系统卸载应用
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	/**
	 * 打开已安装应用的详情
	 */
	public static void goToInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();
		int sdkVersion = Build.VERSION.SDK_INT;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.fromParts("package", packageName, null));
		} else {
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra((sdkVersion == Build.VERSION_CODES.FROYO ? "pkg" : "com.android.settings.ApplicationPkgName"), packageName);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 获取指定程序信息
	 */
	public static ActivityManager.RunningTaskInfo getTopRunningTask(Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// 得到当前正在运行的任务栈
			List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
			// 得到前台显示的任务栈
			ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
			return runningTaskInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getAppVersionCode(Context context) {
		if (context != null) {
			PackageManager pm = context.getPackageManager();
			if (pm != null) {
				PackageInfo pi;
				try {
					pi = pm.getPackageInfo(context.getPackageName(), 0);
					if (pi != null) {
						return pi.versionCode;
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}

	/**
	 * 获取当前系统安装应用的默认位置
	 *
	 * @return APP_INSTALL_AUTO or APP_INSTALL_INTERNAL or APP_INSTALL_EXTERNAL.
	 */
	public static int getInstallLocation() {
		ShellUtil.CommandResult commandResult = ShellUtil.execCommand("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
		if (commandResult.result == 0 && commandResult.responseMsg != null && commandResult.responseMsg.length() > 0) {
			try {
				return Integer.parseInt(commandResult.responseMsg.substring(0, 1));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return APP_INSTALL_AUTO;
	}

	/**
	 * get app package info
	 */
	public static PackageInfo getAppPackageInfo(Context context) {
		if (context != null) {
			PackageManager pm = context.getPackageManager();
			if (pm != null) {
				PackageInfo pi;
				try {
					return pm.getPackageInfo(context.getPackageName(), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * whether context is system application
	 */
	public static boolean isSystemApplication(Context context) {
		if (context == null) {
			return false;
		}
		return isSystemApplication(context, context.getPackageName());
	}

	/**
	 * whether packageName is system application
	 */
	public static boolean isSystemApplication(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		if (packageManager == null || packageName == null || packageName.length() == 0) {
			return false;
		}
		try {
			ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
			return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取已安装的全部应用信息
	 */
	public static List<android.content.pm.PackageInfo> getInsatalledPackages(Context context) {
		return context.getPackageManager().getInstalledPackages(0);
	}

	/**
	 * 获取指定程序信息
	 */
	public static ApplicationInfo getApplicationInfo(Context context, String pkg) {
		try {
			return context.getPackageManager().getApplicationInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定程序信息
	 */
	public static android.content.pm.PackageInfo getPackageInfo(Context context, String pkg) {
		try {
			return context.getPackageManager().getPackageInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 启动应用
	 */
	public static boolean startAppByPackageName(Context context, String packageName) {
		return startAppByPackageName(context, packageName, null);
	}

	/**
	 * 启动应用
	 */
	public static boolean startAppByPackageName(Context context, String packageName, Map<String, String> param) {
		android.content.pm.PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
				resolveIntent.setPackage(pi.packageName);
			}

			List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String packageName1 = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName1, className);

				intent.setComponent(cn);
				if (param != null) {
					for (Map.Entry<String, String> en : param.entrySet()) {
						intent.putExtra(en.getKey(), en.getValue());
					}
				}
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context.getApplicationContext(), "启动失败", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	/**
	 * need < uses-permission android:name =“android.permission.GET_TASKS” />
	 * 判断是否前台运行
	 */
	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName componentName = taskList.get(0).topActivity;
			if (componentName != null && componentName.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断应用是否已安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstalled(Context context, String packageName) {
		boolean hasInstalled = false;
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
		for (PackageInfo p : list) {
			if (packageName != null && packageName.equals(p.packageName)) {
				hasInstalled = true;
				break;
			}
		}
		return hasInstalled;
	}

	/**
	 * 获取文件安装的Intent
	 * 
	 * @param file
	 * @return
	 */
	public static Intent getFileIntent(File file) {
		Uri uri = Uri.fromFile(file);
		String type = "application/vnd.android.package-archive";
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, type);
		return intent;
	}

	// android获取一个用于打开apk文件的intent
	public static Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		return intent;
	}

	/**
	 * 是否需要更新
	 * 
	 * @param pm
	 * @param packageName
	 * @param versionCode
	 * @param versionCode
	 * @param type
	 *            --根据版本号还是版本名称来判断1-为版本号--2为版本名称(名称不同则需要更新)--3为版本名称(
	 *            名称转化为double大于当前则需要更新)
	 * @return
	 */
	public static int getAPKState(PackageManager pm, String packageName, int versionCode, String versionName, int type) {
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : list) {
			String pName = info.packageName;
			int pVersionCode = info.versionCode;
			String pVersionName = info.versionName;
			if (packageName.endsWith(pName)) {
				if (type == 1) {
					if (versionCode == pVersionCode) {
						return INSTALLED;
					} else if (versionCode > pVersionCode) {
						// Log.i("test", "已经安装，有更新");
						return INSTALLED_UPDATE;
					}
				} else if (type == 2) {
					if (versionName.equalsIgnoreCase(pVersionName)) {
						return INSTALLED;
					} else {
						return INSTALLED_UPDATE;
					}
				} else if (type == 3) {
					try {
						int res = compareVersion(pVersionName, versionName);// 整数代表本地版本号大
						if (res < 0) {
							return INSTALLED_UPDATE;
						} else if (res == 0) {
							return INSTALLED;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Log.i("test", "未安装该应用，可以安装");
		return UNINSTALLED;
	}

	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * 
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) throws Exception {
		if (version1 == null || version2 == null) {
			throw new Exception("compareVersion error:illegal params.");
		}
		String[] versionArray1 = version1.split("\\.");// 注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);// 取最小长度值
		int diff = 0;
		while (idx < minLength && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0// 先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {// 再比较字符
			++idx;
		}
		// 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}

	/**
	 * 判断应用是否正在运行
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : list) {
			String processName = appProcess.processName;
			if (processName != null && processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static void installLocalApk(Context context, Uri uri) {
		Intent it = new Intent(Intent.ACTION_VIEW);
		it.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(it);
	}

	/**
	 * 
	 * @Description: 获取程序版本号
	 * @param context
	 * @param packageName
	 *            -包名
	 * @return
	 * @return int
	 * @throw
	 */
	public static int getVersionCode(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(packageName, 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// getPackageName()是你当前类的包名，0代表是获取版本信息String name =
			// pi.versionName;int
			// code = pi.versionCode;
		}
		return 0;
	}

	/**
	 * 
	 * @Description: 获取程序版本号
	 * @param context
	 * @param packageName
	 *            -包名
	 * @return
	 * @return int
	 * @throw
	 */
	public static String getVersionName(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(packageName, 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getCurrentVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
