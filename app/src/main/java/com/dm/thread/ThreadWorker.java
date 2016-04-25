package com.dm.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Dialog;

import com.dm.Consts;
import com.dm.ioc.IocContainer;
import com.dm.ui.dialog.IDialog;

public class ThreadWorker {
	static ExecutorService executorService;

	/**
	 * 
	 * @param runnable
	 * @return
	 */
	public static Future<?> executeRunalle(Runnable runnable) {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(Consts.net_pool_size);
		}
		return executorService.submit(runnable);
	}

	/**
	 * 
	 * @Description: TODO
	 * @param dialog
	 *            -display or hide the progress diaglog when task is busy.
	 * @param task
	 * @return Future<?>
	 * @throws
	 */
	public static Future<?> execuse(boolean dialog, final Task task) {
		if (dialog) {
			IDialog diagloer = IocContainer.getIocContainer().get(IDialog.class);
			Dialog pd = diagloer.showProgressDialog(task.mContext);
			pd.setCancelable(false);
			task.dialog = pd;
		}
		Future<?> future = executeRunalle(new Runnable() {
			@Override
			public void run() {
				try {
					task.doInBackground();
				} catch (Exception e) {
					task.transfer(null, Task.TRANSFER_DOERROR);
					return;
				}
				task.transfer(null, Task.TRANSFER_DOUI);
			}
		});
		return future;
	}

}
