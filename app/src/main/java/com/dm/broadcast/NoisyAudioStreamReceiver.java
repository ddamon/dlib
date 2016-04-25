package com.dm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.dm.logger.Logger;

/**
 * 监听耳机插拔

 *         <p>
 *         //使用以下代码注册广播
 *         <p>
 *         // NoisyAudioStreamReceiver noisyAudioStreamReceiver = new
 *         NoisyAudioStreamReceiver(); // IntentFilter filter = new
 *         IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
 *         filter.addAction(Intent.ACTION_HEADSET_PLUG);
 *         registerReceiver(noisyAudioStreamReceiver, filter);
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {
	private final String TAG = "NoisyAudioStreamReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			int state = intent.getIntExtra("state", -1);
			switch (state) {
			case 0:
				// 拔出耳机
				if (onNoisyStateChangedListener != null) {
					onNoisyStateChangedListener.drawOut();
				}
				Logger.d(TAG, "state:" + state);
				break;
			case 1:
				// 插耳机
				if (onNoisyStateChangedListener != null) {
					onNoisyStateChangedListener.insert();
				}
				Logger.d(TAG, "state:" + state);
				break;
			default:
				Logger.d(TAG, "未知状态");
				break;
			}

		}
		// 只监听拔出耳机
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {

		}
	}

	private OnNoisyStateChangedListener onNoisyStateChangedListener;

	public void OnNoisyStateChangedListener(OnNoisyStateChangedListener onNoisyStateChangedListener) {
		this.onNoisyStateChangedListener = onNoisyStateChangedListener;
	}

	public interface OnNoisyStateChangedListener {
		/* 拔出 */
		void drawOut();

		/* 插入 */
		void insert();
	}
}
