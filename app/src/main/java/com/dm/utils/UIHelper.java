package com.dm.utils;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.GlobalContext;

public class UIHelper {
	/** 网络不可用 */
	public static final int EXCEPTION_INTERNET_FAILED = 0;
	/** 网络可用,但仅使用wifi */
	public static final int EXCEPTION_INTERNET_ONLY_WIFI = 1;

	private static SparseArray<RelativeLayout> maskMap = new SparseArray<RelativeLayout>();

	private static Toast toast = null;

	public static TextView getTextView(Context context, String text, int height, int resId) {
		TextView txt = new TextView(context);
		txt.setText(text);
		txt.setTextColor(Color.BLACK);
		txt.setPadding(5, 0, 5, 0);
		txt.setBackgroundResource(resId);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, height);
		params.setMargins(10, 0, 0, 0);
		txt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		txt.setLayoutParams(params);
		return txt;
	}

	/**
	 * 获得圆角背景的TextView
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static TextView getTextViewForCorners(Context context, String text) {
		return getTextViewForCorners(context, text, LayoutParams.WRAP_CONTENT, 25);
	}

	public static TextView getTextViewForCorners(Context context, String text, int width, int height) {
		TextView txt = new TextView(context);
		txt.setText(text);
		txt.setTextColor(Color.BLACK);
		txt.setPadding(5, 0, 5, 0);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
		params.setMargins(10, 0, 0, 0);
		txt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		txt.setLayoutParams(params);
		return txt;
	}

	public static final int DIRECTION_NEXT = 101;
	public static final int DIRECTION_PREVIOUS = 201;

	/**
	 * 设置为全屏模式，即隐藏标题栏和系统操作栏
	 */
	public static void setFullScreen(Activity activity) {
		// Window window = activity.getWindow();
		// 隐藏标题栏
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏操作栏
		// activity.getWindow().getDecorView().setSystemUiVisibility(View.GONE);
		// 隐藏4.2顶部的状态栏
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// // Hide status bar
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// // Show status bar
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 隐藏/显示 系统操作栏
	 */
	public static void toogleSystemBar(Activity activity) {
		Window window = activity.getWindow();
		if (View.GONE == window.getDecorView().getSystemUiVisibility()) {
			window.getDecorView().setSystemUiVisibility(View.VISIBLE);
		} else {
			window.getDecorView().setSystemUiVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏和显示view
	 * 
	 * @param view
	 * @param space
	 *            is still takes up space for layout purposes
	 */
	public static void toogleVisibility(View view, boolean space) {
		if (view.getVisibility() == View.VISIBLE) {
			if (space) {
				view.setVisibility(View.INVISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}

		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Toast提示消息
	 * 
	 * @param context
	 * @param text
	 * @param isShort
	 *            LENGTH_SHORT
	 */
	public static void makeToast(Context context, CharSequence text, boolean isShort) {
		int duration = isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
		// Toast.makeText(context, text, duration).show();
		if (toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);
			toast.setDuration(duration);
		}
		toast.show();
	}

	public static void makeToast(Context context, CharSequence text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}

	/**
	 * Toast提示消息
	 * 
	 * @param context
	 * @param resId
	 * @param isShort
	 *            LENGTH_SHORT
	 */
	public static void makeToast(Context context, int resId, boolean isShort) {
		int duration = isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
		/*
		 * int duration = isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
		 * 
		 * Toast.makeText(context, resId, duration).show();
		 */
		if (toast == null) {
			toast = Toast.makeText(context, resId, duration);
		} else {
			toast.setText(resId);
			toast.setDuration(duration);
		}
		toast.show();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) (dpValue * metrics.density + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) (pxValue / metrics.density + 0.5f);
	}

	/**
	 * 
	 * @Description: dynamic setting the height of a listview
	 * @param @param listView
	 * @return void
	 * @throws
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/** 获取屏幕宽 */
	public static int getDisplayWidth(Activity activity) {
		final DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);// 获取分辨率
		return metric.widthPixels;
	}

	/**
	 * 获取屏幕高 px
	 */
	public static int getDisplayHeight(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();// 获取分辨率
		return metric.heightPixels;
	}

	/**
	 * 获取屏幕高 px
	 */
	public static int getDisplayWidth(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();// 获取分辨率
		return metric.widthPixels;
	}

	/**
	 * @author 获取屏幕高
	 */
	public static int getDisplayHeight(Activity activity) {
		final DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);// 获取分辨率
		return metric.heightPixels;
	}

	/**
	 * hide the keyboard
	 * 
	 * @param activity
	 */
	public static void hideSoftInputFromWindow(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * hide the keyboard
	 * 
	 * @param view
	 */
	public static void hideSoftInputFromWindow(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * display the keyboard
	 * 
	 * @param context
	 * @param view
	 */
	public static void showInputMethodFromView(Context context, View view) {
		InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.showSoftInput(view, 0);
	}

	/**
	 * 设置文本选中颜色 选中时为白色 反正为黑色
	 * 
	 * @param selected
	 *            是否选中
	 * @param views
	 */
	public static void setTextColor(boolean selected, TextView... views) {
		for (int i = 0; i < views.length; i++) {
			views[i].setTextColor(selected ? Color.WHITE : Color.BLACK);
		}
	}

	/**
	 * 
	 * @Description: keep the dialog showing
	 * @param @param dialog
	 * @return void
	 * @throws
	 */
	public static void keepDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void distroyDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * copy text from clipboard
	 * 
	 * @param label
	 * @param text
	 */
	public static void copyPlainText(Context context, CharSequence label, CharSequence text) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(label, text);
		clipboard.setPrimaryClip(clip);
	}

	/**
	 * share text information
	 * 
	 * @param title
	 * @param value
	 */
	public static void shareText(Context context, String title, String value) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, value);
		// sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, title));
	}

	/**
	 * 当按返回键的时候调用
	 */
	public static void exit(OnBackKeyListener onBackKeyListener) {
		onBackKeyListener.option();
	}

	interface OnBackKeyListener {
		public boolean option();
	}

	/**
	 * force to show overflow menu in actionbar for android 4.4 below
	 * 
	 * @param context
	 */
	public static void getOverflowMenu(Context context) {
		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description: get resource id
	 * @param @param resName
	 * @param @param defType
	 * @param @return
	 * @return int
	 * @throws
	 */
	public static int getResId(String resName, String defType) {
		try {
			String packageName = GlobalContext.getInstance().getPackageName();
			Resources resources = GlobalContext.getInstance().getPackageManager().getResourcesForApplication(packageName);
			int resId = resources.getIdentifier(resName, defType, packageName);
			return resId;
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 控制scrollview滚动到底部或者顶部
	 * 
	 * @param scrollView
	 * @param flag
	 *            0顶部
	 */
	public static void scroll(ScrollView scrollView, int flag) {
		if (flag == 0) {
			scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶部

		} else {
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底部
		}
	}

	/**
	 * 获取焦点
	 * 
	 * @param view
	 */
	public static void requestFocus(View view) {
		if (view == null) {
			return;
		}
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}

	/**
	 * 获取屏幕尺寸
	 * 
	 * @param activity
	 *            Activity
	 * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
	 */
	public static int[] getScreenSize(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return new int[] { metrics.widthPixels, metrics.heightPixels };
	}
	/**
	 * 
	* @Description: 设置光标位置
	* @param et
	* @param offset
	* @return void
	* @throw
	 */
	public static void setEditTextSursor(EditText et,int offset){
		if (et==null) {
			return;
		}
		et.setSelection(offset);
	}
}
