package com.dm.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

/**
 * 对话框接口
 */
public interface IDialog {

	public static int YES = -1;

	public static int CANCLE = -2;

	/**
	 * 根据isCancel判断对话框是否能取消
	 * 
	 * @Description: TODO
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnConfirm
	 * @param btnCancel
	 * @param isCancel
	 *            --是否可以取消
	 * @param dialogCallBack
	 * @return
	 * @return Dialog
	 * @throw
	 */
	public Dialog showDialog(Context context, String title, String msg, String btnConfirm, String btnCancel, boolean isCancel, final DialogCallBack dialogCallBack);

	/**
	 * 不能取消的对话框
	 * 
	 * @Description: TODO
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnConfirm
	 * @param btnCancel
	 * @param dialogCallBack
	 * @return
	 * @return Dialog
	 * @throw
	 */
	public Dialog showDialogNoCancel(Context context, String title, String msg, String btnConfirm, String btnCancel, final DialogCallBack dialogCallBack);

	/**
	 * 普通对话框
	 * 
	 * @Description: TODO
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnConfirm
	 * @param btnCancel
	 * @param dialogCallBack
	 * @return
	 * @return Dialog
	 * @throw
	 */
	public Dialog showDialog(Context context, String title, String msg, String btnConfirm, String btnCancel, final DialogCallBack dialogCallBack);

	/**
	 * 显示带有单个按钮且无法点击返回键取消的对话框
	 * 
	 * @Description: TODO
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnConfirm
	 * @param dialogCallBack
	 * @return
	 * @return Dialog
	 * @throw
	 */
	public Dialog showDialogSingleBtn(Context context, String title, String msg, String btnConfirm, final DialogCallBack dialogCallBack);

	/**
	 * 显示短的toast
	 * 
	 * @param context
	 * @param msg
	 */
	public void showToastLong(Context context, String msg);

	/**
	 * 显示长的toast
	 * 
	 * @param context
	 * @param msg
	 */
	public void showToastShort(Context context, String msg);

	/**
	 * 根据type显示toast
	 * 
	 * @param context
	 * @param type
	 */
	public void showToastType(Context context, String msg, String type);

	/**
	 * 显示确定对话框 (确定,取消)
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param callback
	 */
	public Dialog showDialog(Context context, String title, String msg, DialogCallBack callback);

	/**
	 * 显示确定对话框 (确定,取消) 可指定icon
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param callback
	 */
	public Dialog showDialog(Context context, int icon, String title, String msg, DialogCallBack callback);

	/**
	 * item选择对话框
	 * 
	 * @param context
	 * @param title
	 * @param items
	 * @param callback
	 */
	public Dialog showItemDialog(Context context, String title, CharSequence[] items, DialogCallBack callback);

	/**
	 * 显示进度框,可以返回
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @return
	 */
	public Dialog showProgressDialog(Context context, String title, String msg, boolean cancel);

	/**
	 * 显示进度框,不可返回
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public Dialog showProgressDialog(Context context, String msg);

	/**
	 * 显示进度框
	 * 
	 * @param context
	 * @return
	 */
	public Dialog showProgressDialog(Context context);

	/**
	 * 用adapter 的dialoger
	 * 
	 * @param adapter
	 * @param itemClickListener
	 * @return
	 */
	public Dialog showAdapterDialoge(Context context, String title, ListAdapter adapter, OnItemClickListener itemClickListener);

}
