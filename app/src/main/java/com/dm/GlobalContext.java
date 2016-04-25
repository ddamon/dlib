package com.dm;

import android.app.ActivityManager;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import com.dm.dlib.R;
import com.dm.ioc.Instance.InstanceScope;
import com.dm.ioc.Ioc;
import com.dm.ioc.IocContainer;
import com.dm.logger.Logger;
import com.dm.ui.dialog.DialogImpl;
import com.dm.ui.dialog.IDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 必须继承此context
 */
public class GlobalContext extends Application {

	private static GlobalContext _context;

	@Override
	public void onCreate() {
		super.onCreate();
		_context = this;
		// 初始化IOC
		Ioc.initApplication(_context);
		// 对话框的配置
		Ioc.bind(DialogImpl.class).to(IDialog.class).scope(InstanceScope.SCOPE_PROTOTYPE);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()//
		.showImageForEmptyUri(R.drawable.empty_photo)//
				.showImageOnFail(R.drawable.empty_photo)//
//				.cacheInMemory(true)//
				.cacheOnDisk(true)//
				.bitmapConfig(Bitmap.Config.RGB_565)//
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())//
				.threadPoolSize(3)// 线程池的线程数目-默认是3
				.defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config);

		// 注入初始设置
		IocContainer.getIocContainer().bind(ConfigSetting.class).to(ConfigSetting.class).scope(InstanceScope.SCOPE_SINGLETON);
		ConfigSetting cs = IocContainer.getIocContainer().get(ConfigSetting.class);
		cs.load();
		// 数据库初始化
		// DB db = IocContainer.getIocContainer().get(DB.class);
		// db.init("dhdbname", ConfigSetting.DATABASE_VERSION);

	}

	public static GlobalContext getInstance() {
		return _context;
	}

	// 内存相关
	public void displayBriefMemory() {
		final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		Logger.i("System.out", "系统剩余内存:" + (info.availMem >> 20) + "MB");
		Logger.i("System.out", "系统是否处于低内存运行：" + info.lowMemory);
		Logger.i("System.out", "当系统剩余内存低于" + (info.threshold >> 20) + "MB时就看成低内存运行");
	}

	/**
	 * 检查SD卡是否存在
	 * 
	 * @return boolean
	 */
	public boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return 路径
	 */
	public static String getExternalStoragePath() {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory().getPath();
			}
		}
		return null;
	}

	/**
	 * 　 获取存储卡的剩余容量，单位为字节 　　
	 * 
	 * @param filePath
	 * @return availableSpare 　　
	 */
	public static long getAvailableStore(String filePath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}
}