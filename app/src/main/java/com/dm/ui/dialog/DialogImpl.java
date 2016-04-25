package com.dm.ui.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.dlib.R;
import com.dm.ioc.IocContainer;
import com.dm.utils.DataValidation;

/***
 * IDialog 基本实现
 * 
 * 
 * 
 */
public class DialogImpl implements IDialog {
	/**
	 * 显示带有单个按钮且无法点击返回键取消的对话框
	 */
	public Dialog showDialogSingleBtn(Context context, String title, String msg, String btnConfirm, final DialogCallBack dialogCallBack) {
		Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setNegativeButton(btnConfirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialogCallBack != null) {
					dialogCallBack.onClick(IDialog.YES);
				}
			}
		});
		builder.setCancelable(false);
		return builder.show();
	}

	/**
	 * isCancel-是否取消
	 */
	public Dialog showDialog(Context context, String title, String msg, String btnConfirm, String btnCancel, boolean isCancel, final DialogCallBack dialogCallBack) {
		return isCancel ? showDialog(context, title, msg, btnConfirm, btnCancel, dialogCallBack) : showDialogNoCancel(context, title, msg, btnConfirm, btnCancel, dialogCallBack);
	}

	/**
	 * 显示自定义确定和取消按钮的对话框，对话框无法点击返回键取消
	 */
	public Dialog showDialogNoCancel(Context context, String title, String msg, String btnConfirm, String btnCancel, final DialogCallBack dialogCallBack) {
		Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setNegativeButton(btnConfirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialogCallBack != null) {
					dialogCallBack.onClick(IDialog.YES);
				}
			}
		});
		if (dialogCallBack != null) {
			builder.setPositiveButton(btnCancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialogCallBack != null) {
						dialogCallBack.onClick(IDialog.CANCLE);
					}
				}
			});
		}
		builder.setCancelable(false);
		return builder.show();
	}

	public Dialog showDialog(Context context, String title, String msg, String btnConfirm, String btnCancel, final DialogCallBack dialogCallBack) {
		Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setNegativeButton(btnConfirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialogCallBack != null) {
					dialogCallBack.onClick(IDialog.YES);
				}
			}
		});
		if (!DataValidation.isEmpty(btnCancel)) {
			builder.setPositiveButton(btnCancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialogCallBack != null) {
						dialogCallBack.onClick(IDialog.CANCLE);
					}
				}
			});
		}
		return builder.show();
	}

	public Dialog showDialog(Context context, String title, String msg, final DialogCallBack dialogCallBack) {
		Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setNegativeButton(context.getString(R.string.confirm), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialogCallBack != null) {
					dialogCallBack.onClick(IDialog.YES);
				}
			}
		});
		builder.setPositiveButton(context.getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialogCallBack != null) {
					dialogCallBack.onClick(IDialog.CANCLE);
				}
			}
		});
		return builder.show();
	}

	public Dialog showItemDialog(Context context, String title, CharSequence[] items, final DialogCallBack callback) {
		Dialog dialog = new AlertDialog.Builder(context).setTitle(title).setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.onClick(which);
				}
			}
		}).show();
		return dialog;

	}

	class DialogOnItemClickListener implements OnItemClickListener {
		Dialog dialog;

		public void setDialog(Dialog dialog) {
			this.dialog = dialog;
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		}

	}

	public Dialog showProgressDialog(Context context, String title, String msg, boolean cancel) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMessage(msg);
		progressDialog.show();
		progressDialog.setCancelable(cancel);
		return progressDialog;
	}

	public Dialog showProgressDialog(Context context, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(msg);
		progressDialog.show();
		progressDialog.setCancelable(true);
		return progressDialog;
	}

	public Dialog showProgressDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.show();
		progressDialog.setCancelable(true);
		return progressDialog;
	}

	public void showToastLong(Context context, String msg) {
		// 使用同一个toast避免 toast重复显示
		Toast toast = IocContainer.getIocContainer().get(Toast.class);
		toast.setDuration(Toast.LENGTH_LONG);
		TextView textView = new TextView(context);
		textView.setText(msg);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(15, 10, 15, 10);
		textView.setBackgroundResource(android.R.drawable.toast_frame);
		toast.setView(textView);
		toast.show();
	}

	public void showToastShort(Context context, String msg) {
		// 使用同一个toast避免 toast重复显示
		Toast toast = IocContainer.getIocContainer().get(Toast.class);
		toast.setDuration(Toast.LENGTH_SHORT);
		TextView textView = new TextView(context);
		textView.setText(msg);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(15, 10, 15, 10);
		textView.setBackgroundResource(android.R.drawable.toast_frame);
		toast.setView(textView);
		toast.show();
	}

	public void showToastType(Context context, String msg, String type) {
		showToastLong(context, msg);
	}

	public Dialog showDialog(Context context, int icon, String title, String msg, DialogCallBack callback) {
		return showDialog(context, title, msg, callback);
	}

	public Dialog showAdapterDialoge(Context context, String title, ListAdapter adapter, OnItemClickListener itemClickListener) {
		// Dialog dialog=new ListDialog(context, title, adapter,
		// itemClickListener);
		// dialog.show();
		return null;
	}
}
