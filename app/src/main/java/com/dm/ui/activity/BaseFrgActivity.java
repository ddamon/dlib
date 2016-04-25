package com.dm.ui.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.dm.Consts;
import com.dm.dlib.R;
import com.dm.ioc.InjectUtil;
import com.dm.logger.Logger;
import com.dm.thread.Task;
import com.dm.thread.ThreadWorker;
import com.dm.ui.fragment.BaseFragment;
import com.dm.ui.widget.TitleHeaderBar;
import com.dm.utils.DataValidation;
import com.dm.utils.UIHelper;

/**
 * 
 * @Description: Main Activity
 * @author damon.han
 * @date 2015年1月25日 上午11:13:21
 */
public class BaseFrgActivity extends FragmentActivity implements IBaseActivity {

	public static final int VISIBLE = View.VISIBLE;
	public static final int GONE = View.GONE;
	public static final int INVISIBLE = View.INVISIBLE;

	public static final String TAG = "BaseFrgActivity";
	private int theme = 0;
	private boolean isDestory;
	// 当有Fragment Attach到这个Activity的时候，就会保存
	private Map<String, WeakReference<BaseFragment>> fragmentRefs;
	private static BaseFrgActivity runningActivity;
	private View rootView;
	/* title bar */
	protected TitleHeaderBar mTitleHeaderBar;
	private ActivityStack stack = ActivityStack.getInstanse();

	protected Context mContext = null;

	public static BaseFrgActivity getRunningActivity() {
		return runningActivity;
	}

	public static void setRunningActivity(BaseFrgActivity activity) {
		runningActivity = activity;
	}

	protected int configTheme() {
		// return CommSettings.getAppTheme();
		return 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		stack.addActivity(this);
		fragmentRefs = new HashMap<String, WeakReference<BaseFragment>>();
		if (savedInstanceState == null) {
			theme = configTheme();
		} else {
			theme = savedInstanceState.getInt("theme");
		}
		// 设置主题
		if (theme > 0) {
			setTheme(theme);
		}

	}

	@Override
	public void setContentView(int layoutResID) {
		rootView = LayoutInflater.from(this).inflate(layoutResID, null);
		super.setContentView(rootView);
		if (Consts.auto_inject) {
			InjectUtil.inject(this);
		}
		mTitleHeaderBar = (TitleHeaderBar) rootView.findViewById(R.id.lib_common_bar);
		if (mTitleHeaderBar != null) {
			if (enableDefaultBack()) {
				mTitleHeaderBar.setBackgroundColor(getResources().getColor(R.color.lib_common_bar_bg));
				View view = mTitleHeaderBar.getLeftImageView();
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackClick();
					}
				});

			} else {
				mTitleHeaderBar.getLeftViewContainer().setVisibility(View.INVISIBLE);
			}
		}
	}

	public View getRootView() {
		return rootView;
	}

	public void addFragment(String tag, BaseFragment fragment) {
		fragmentRefs.put(tag, new WeakReference<BaseFragment>(fragment));
	}

	public void removeFragment(String tag) {
		fragmentRefs.remove(tag);
	}

	@Override
	protected void onResume() {
		super.onResume();
		runningActivity = this;
		if (theme == configTheme()) {

		} else {
			Logger.i(TAG, "theme changed, reload()");
			reload();
			return;
		}

	}

	/**
	 * 设置语言
	 * 
	 * @Description: TODO
	 * @param locale
	 * @return void
	 * @throw
	 */
	public void setLanguage(Locale locale) {
		Resources resources = getResources();// 获得res资源对象
		Configuration config = resources.getConfiguration();// 获得设置对象
		config.locale = locale;
		DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
		resources.updateConfiguration(config, dm);
	}

	/**
	 * 重启程序
	 */
	public void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		isDestory = true;
		super.onDestroy();
	}

	public boolean onHomeClick() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<BaseFragment> fragmentRef = fragmentRefs.get(key);
			BaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment.onHomeClick())
				return true;
		}
		finish();
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (onBackClick())
				return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onBackClick() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<BaseFragment> fragmentRef = fragmentRefs.get(key);
			BaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment.onBackClick())
				return true;
		}
		finish();
		return true;
	}

	public boolean onAcUnusedDoubleClicked() {
		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<BaseFragment> fragmentRef = fragmentRefs.get(key);
			BaseFragment fragment = fragmentRef.get();
			if (fragment != null && fragment instanceof IAcUnusedDoubleClickedHandler) {
				if (((IAcUnusedDoubleClickedHandler) fragment).onAcUnusedDoubleClicked())
					return true;
			}
		}
		return false;
	}

	@Override
	public void finish() {
		setMDestory(true);
		super.finish();
		stack.removeActivity(this);
	}

	public boolean mIsDestoryed() {
		return isDestory;
	}

	public void setMDestory(boolean destory) {
		this.isDestory = destory;
	}

	public interface IAcUnusedDoubleClickedHandler {
		public boolean onAcUnusedDoubleClicked();

	}

	public void addTask(Task task) {
		ThreadWorker.execuse(true, task);
	}

	/**
	 * 
	 * @Description: 堆栈中是否存在某个activity
	 * @param cName全路径
	 * @return boolean
	 * @throws
	 */
	public boolean isExistActivity(String cName) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getClassName().contains(cName)) {
			isTop = true;
		}
		return isTop;
	}

	protected boolean enableDefaultBack() {
		return true;
	}

	protected void setHeaderTitle(int id) {
		mTitleHeaderBar.getTitleTextView().setText(id);
	}

	protected void setHeaderTitle(String title) {
		mTitleHeaderBar.getTitleTextView().setText(title);
	}

	public TitleHeaderBar getTitleHeaderBar() {
		return mTitleHeaderBar;
	}

	/**
	 * 判断是否有网络连接,没有返回false
	 */
	@Override
	public boolean hasInternetConnected() {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络连接，若没有，则弹出网络设置对话框，返回false
	 */
	@Override
	public boolean validateInternet() {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			openWirelessSet();
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		openWirelessSet();
		return false;
	}

	/**
	 * 判断GPS定位服务是否开启
	 */
	@Override
	public boolean hasLocationGPS() {
		LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断基站定位是否开启
	 */
	@Override
	public boolean hasLocationNetWork() {
		LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查内存卡可读
	 */
	@Override
	public void checkMemoryCard() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			new AlertDialog.Builder(mContext).setTitle("检测内存卡").setMessage("请检查内存卡").setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					mContext.startActivity(intent);
				}
			}).setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();

				}
			}).create().show();
		}
	}

	/**
	 * 打开网络设置对话框
	 */
	public void openWirelessSet() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle("网络设置").setMessage("检查网络").setPositiveButton("网络设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				mContext.startActivity(intent);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		dialogBuilder.show();
	}

	/**
	 * 关闭键盘
	 */
	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && this.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 返回上下文对象
	 */
	@Override
	public Context getContext() {
		return mContext;
	}

	@Override
	public void initUncaughtExceptionHandler() {
		// TODO Auto-generated method stub

	}

	/**
	 * show toast
	 * 
	 * @param msg
	 */
	public void showToast(String msg) {
		if (!DataValidation.isEmpty(msg)) {
			UIHelper.makeToast(mContext, msg);
		}
	}

	public void showToast(int strId) {
		UIHelper.makeToast(mContext, mContext.getString(strId));
	}

	/**
	 * 从当前activity跳转到目标activity,<br>
	 * 如果目标activity曾经打开过,就重新展现,<br>
	 * 如果从来没打开过,就新建一个打开
	 * 
	 * @param cls
	 */
	public void gotoExistActivity(Class<?> cls) {
		Intent intent;
		intent = new Intent(this, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	/**
	 * 从当前activity跳转到目标activity,<br>
	 * 如果目标activity曾经打开过,就重新展现,<br>
	 * 如果从来没打开过,就新建一个打开
	 * 
	 * @param cls
	 */
	public void gotoExistActivityWithBundle(Class<?> cls, Bundle bundle) {
		Intent intent;
		intent = new Intent(this, cls);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	/**
	 * 新建一个activity打开
	 * 
	 * @param cls
	 */
	public void gotoActivity(Class<?> cls) {
		Intent intent;
		intent = new Intent(this, cls);
		startActivity(intent);
	}

	/**
	 * 新建一个activity打开
	 * 
	 * @param cls
	 */
	public void gotoActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent(this, cls);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void gotoActivity(Class<?> cls, int enterAnim, int exitAnim) {
		gotoActivity(cls);
		overridePendingTransition(enterAnim, exitAnim);
	}

	/**
	 * 
	 * @param cls
	 * @param bundle
	 *            携带数据
	 * @param enterAnim
	 *            进入动画
	 * @param exitAnim离开动画
	 */
	public void gotoActivity(Class<?> cls, Bundle bundle, int enterAnim, int exitAnim) {
		gotoActivity(cls, bundle);
		overridePendingTransition(enterAnim, exitAnim);
	}

	public void gotoActivityWithDefaultAnmi(Class<?> cls, Bundle bundle) {
		gotoActivity(cls, bundle);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	public void gotoActivityWithDefaultAnmi(Class<?> cls) {
		gotoActivity(cls);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mActivityResultListener != null) {
			mActivityResultListener.onActivityResult(requestCode, resultCode, data);
		}
	}

	private OnActivityResultListener mActivityResultListener;

	public void setOnActivityResultListener(OnActivityResultListener l) {
		this.mActivityResultListener = l;
	}

	public void hideLeft() {
		mTitleHeaderBar.getLeftViewContainer().setVisibility(GONE);
	}

	public void hideRight() {
		mTitleHeaderBar.getRightViewContainer().setVisibility(GONE);
	}

	/**
	 * 切换显示状态
	 * 
	 * @param view
	 */
	public void toggleView(View view) {
		if (view == null) {
			return;
		}
		if (view.getVisibility() == GONE) {
			view.setVisibility(VISIBLE);
		} else {
			view.setVisibility(GONE);
		}
	}

	@Override
	public void startService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopService() {
		// TODO Auto-generated method stub

	}

}
