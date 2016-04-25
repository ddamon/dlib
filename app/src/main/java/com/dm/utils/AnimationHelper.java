package com.dm.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.dm.dlib.R;

/**
 * AnimationHelper
 * 
 */
public class AnimationHelper {
	public static final String TAG = "AnimationHelper";
	public static final int SLIDE_TO_TOP = 1;
	public static final int SLIDE_TO_BOTTOM = 2;
	private static AnimationSet set;
	private static Animation animation;

	/**
	 * ListView的显示效果
	 * 
	 * @param listView
	 */
	public static void applyListViewAnimation(ListView listView) {
		if (listView == null) {
			Log.d(TAG, "listView为空,应用效果出错");
			return;
		}
		set = new AnimationSet(true);
		animation = new AlphaAnimation(0.0f, 1.0f);// 透明到实质
		animation.setDuration(100);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(200);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);// 延迟
		listView.setLayoutAnimation(controller);
	}

	/**
	 * view渐隐动画
	 * 
	 * @param v
	 * @param duration
	 *            -动画持续时间
	 */
	public static void setHideAnimation(View v, int duration) {
		if (null == v || duration < 0) {
			return;
		}
		if (null != animation) {
			animation.cancel();
		}
		animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		v.startAnimation(animation);
	}

	/**
	 * View渐现动画效果
	 */
	public static void setShowAnimation(View view, int duration) {
		if (null == view || duration < 0) {
			return;
		}
		if (null != animation) {
			animation.cancel();
		}
		animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}

	/**
	 * 切换隐藏显示
	 * 
	 * @param view
	 * @param direction
	 */
	public static void toogleSlide(final ViewGroup view, int direction) {
		Animation animation = null;
		int xtype = Animation.RELATIVE_TO_SELF;
		switch (direction) {
		case SLIDE_TO_TOP:
			if (view.getVisibility() == View.GONE) {
				animation = new TranslateAnimation(xtype, 0.0f, xtype, 0.0f, xtype, -1.0f, xtype, 0.0f);
			} else {
				animation = new TranslateAnimation(xtype, 0.0f, xtype, 0.0f, xtype, 0.0f, xtype, -1.0f);
			}
			break;
		case SLIDE_TO_BOTTOM:
			if (view.getVisibility() == View.GONE) {
				animation = new TranslateAnimation(xtype, 0.0f, xtype, 0.0f, xtype, 1.0f, xtype, 0.0f);
			} else {
				animation = new TranslateAnimation(xtype, 0.0f, xtype, 0.0f, xtype, 0.0f, xtype, 1.0f);
			}
			break;
		default:
			break;
		}
		animation.setDuration(500);
		if (view.getVisibility() == View.GONE) {
			view.startAnimation(animation);
			view.setVisibility(View.VISIBLE);
		} else {
			view.startAnimation(animation);
			view.setVisibility(View.GONE);
		}
		// view.setLayoutAnimation(controller)
	}

	/**
	 * 水平位置移位
	 * 
	 * @param view
	 * @param fromXDelta
	 * @param toXDelta
	 */
	public static void slideHorizontal(final View view, float fromXDelta, float toXDelta) {
		TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
		animation.setInterpolator(new OvershootInterpolator());
		animation.setDuration(500);
		animation.setStartOffset(0);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}

	public static final int LEFT_IN = 1;
	public static final int LEFT_OUT = 2;
	public static final int RIGHT_IN = 3;
	public static final int RIGHT_OUT = 4;

	public static void flip(Context context, ViewFlipper flipper, int direction) {
		switch (direction) {
		case LEFT_IN:
			flipper.setInAnimation(context, R.anim.flipper_left_in);
			flipper.setOutAnimation(context, R.anim.flipper_none);
			setAnimationListener(flipper);
			flipper.showNext();
			break;
		case LEFT_OUT:
			flipper.setInAnimation(context, R.anim.flipper_none);
			flipper.setOutAnimation(context, R.anim.flipper_left_out);
			setAnimationListener(flipper);
			flipper.showPrevious();
			break;
		case RIGHT_IN:
			flipper.setInAnimation(context, R.anim.flipper_right_in);
			flipper.setOutAnimation(context, R.anim.flipper_none);
			setAnimationListener(flipper);
			flipper.showNext();
			break;
		case RIGHT_OUT:
			flipper.setInAnimation(context, R.anim.flipper_none);
			flipper.setOutAnimation(context, R.anim.flipper_right_out);
			setAnimationListener(flipper);
			flipper.showPrevious();
			break;
		default:
			break;
		}
	}

	public static boolean isInAnimation(ViewFlipper flipper) {
		Object obj = flipper.getTag(R.id.animationEnd);
		if (obj == null) {
			flipper.setTag(R.id.animationEnd, 0);
			return false;
		} else {
			flipper.setTag(R.id.animationEnd, 0);
			return obj.toString().equalsIgnoreCase("0");
		}
	}

	private static void setAnimationListener(final ViewFlipper flipper) {
		Animation animation = flipper.getInAnimation();
		if (animation == null)
			return;
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				flipper.setTag(R.id.animationEnd, 0);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				flipper.setTag(R.id.animationEnd, 1);
			}
		});
	}

	/**
	 * 默认是滑动显示
	 * 
	 * @param context
	 * @param flipper
	 * @param child
	 */
	public static void setDisplayedChild(Context context, ViewFlipper flipper, int child) {
		int current = flipper.getDisplayedChild();
		if (child > current) {
			flipper.setInAnimation(context, R.anim.flipper_right_in);
			flipper.setOutAnimation(context, R.anim.flipper_left_out);
		} else {
			flipper.setInAnimation(context, R.anim.flipper_left_in);
			flipper.setOutAnimation(context, R.anim.flipper_right_out);
		}
		flipper.setDisplayedChild(child);
	}
}
