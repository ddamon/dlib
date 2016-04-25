package com.dm.utils;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * 放大缩小 拖动等
 * 
 * @author Michael.li
 * 
 */
public class ZoomHelper {
	private Context mContext;

	/**
	 * 
	 * @param context
	 * @param visibleWidth
	 *            px
	 * @param visibleHeight
	 *            px
	 */
	public ZoomHelper(Context context, float visibleWidth, float visibleHeight) {
		mContext = context;
		mVisibleWidth = visibleWidth;
		mVisibleHeight = visibleHeight;
		MAX_SCALE = UIHelper.dip2px(context, MAX_SCALE);

		mIsLongpressEnabled = true;
		// Fallback to support pre-donuts releases
		int touchSlop, doubleTapSlop;
		ViewConfiguration configuration = ViewConfiguration.get(context);
		touchSlop = configuration.getScaledTouchSlop();
		doubleTapSlop = configuration.getScaledDoubleTapSlop();
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		mTouchSlopSquare = touchSlop * touchSlop;
		mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
	}

	/**
	 * 设置需要缩放的ImageView以及Bitmap对应的宽度和高度
	 * 
	 * @param imageView
	 * @param width
	 *            px
	 * @param height
	 *            px
	 */
	public void setImageView(ImageView imageView, float width, float height) {
		mBitmapWidth = width;
		mBitmapHeight = height;
		mImageView = imageView;
		mImageView.setOnTouchListener(onTouchListener);

		mCurrentMatrix = new Matrix();
		mSavedMatrix = new Matrix();

		minZoom();
		center();
		mImageView.setImageMatrix(mCurrentMatrix);

		Log.i(TAG, "mVisibleWidth:" + mVisibleWidth + "mVisibleHeight:" + mVisibleHeight);
		Log.i(TAG, "mBitmapWidth:" + mBitmapWidth + "mBitmapHeight:" + mBitmapHeight);
	}

	private int mTouchSlopSquare;
	private int mDoubleTapSlopSquare;
	private int mMinimumFlingVelocity;
	private int mMaximumFlingVelocity;

	private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
	private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
	private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();

	// constants for Message.what used by GestureHandler below
	private static final int MSG_SHOW_PRESS = 1;
	private static final int MSG_LONG_PRESS = 2;// 长按
	private static final int MSG_TAP = 3;// 单击
	private static final int MSG_DOUBLE_TAP = 4;// 双击
	private static final int MSG_ZOOM_EXIT = 5;// 缩小退出

	private boolean mStillDown;
	private boolean mInLongPress;
	private boolean mAlwaysInTapRegion;
	private boolean mAlwaysInBiggerTapRegion;

	private MotionEvent mCurrentDownEvent;
	private MotionEvent mPreviousUpEvent;

	/**
	 * True when the user is still touching for the second tap (down, move, and
	 * up events). Can only be true if there is a double tap listener attached.
	 */
	private boolean mIsDoubleTapping;

	private float mLastFocusX;
	private float mLastFocusY;
	private float mDownFocusX;
	private float mDownFocusY;

	private boolean mIsLongpressEnabled;

	/**
	 * Determines speed during touch scrolling
	 */
	private VelocityTracker mVelocityTracker;

	private static final String TAG = "ZoomHelper";

	private final Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_PRESS:
				// mListener.onShowPress(mCurrentDownEvent);
				break;

			case MSG_LONG_PRESS:
				dispatchLongPress();
				break;

			case MSG_TAP:
				// If the user's finger is still down, do not count it as a tap
				if (mListener != null && !mStillDown) {
					mListener.onEventDetected(OnZoomGestureListener.EVENT_TAP);
				}
				break;
			case MSG_DOUBLE_TAP:
				onDoubleTap();
				mode = NONE;
				center();
				break;
			case MSG_ZOOM_EXIT:
				if (mListener != null) {
					mListener.onEventDetected(OnZoomGestureListener.EVENT_EXIT);
				}
				break;
			default:
				throw new RuntimeException("Unknown message " + msg); // never
			}
			return false;
		}
	});

	private void onDoubleTap() {
		if (!mCanZoom) {
			return;
		}
		// onHighlightHide(OnZoomGestureListener.MSG_HIDE);
		boolean scaled = hasScaled(mCurrentMatrix);
		mCurrentMatrix = new Matrix();
		if (scaled) {
			// FIXME 恢复到初始大小 后期可以添加动画效果
			// matrix.postScale(mScaleMini, mScaleMini);
			mCurrentMatrix.postScale(mScaleX, mScaleY);
			return;
		}
		// 双击后放大一倍，并以双击的点为中心
		float width = mBitmapWidth * mScaleX;
		float height = mBitmapHeight * mScaleY;

		// float currentScale = MAX_SCALE;// mScaleMini * 2;
		// TODO 放大处理

		float transX = prev.x * width / mVisibleWidth;
		float transY = prev.y * height / mVisibleHeight;
		Log.i(TAG, "transX:" + transX + " transY" + transY);
		// FIXME 边缘处理 九宫格 待优化处理为圆中心
		if (transX < width / 3) {
			transX = 0;
		} else if (width - transX < width / 3) {
			transX = width;
		} else {
			//
		}
		if (transY < height / 3) {
			transY = 0;
		} else if (height - transY < height / 3) {
			transY = height;
		} else {

		}
		mCurrentMatrix.postScale(mScaleX * 2, mScaleY * 2);// 放大一倍
		mCurrentMatrix.postTranslate(-transX, -transY);
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent ev) {
			final int action = ev.getAction();
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);

			final boolean pointerUp = (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
			final int skipIndex = pointerUp ? ev.getActionIndex() : -1;

			// Determine focal point
			float sumX = 0, sumY = 0;
			final int count = ev.getPointerCount();
			for (int i = 0; i < count; i++) {
				if (skipIndex == i)
					continue;
				sumX += ev.getX(i);
				sumY += ev.getY(i);
			}
			final int div = pointerUp ? count - 1 : count;
			final float focusX = sumX / div;
			final float focusY = sumY / div;

			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				mDownFocusX = mLastFocusX = focusX;
				mDownFocusY = mLastFocusY = focusY;
				onActionPointerDown(ev);
				break;

			case MotionEvent.ACTION_POINTER_UP:
				mDownFocusX = mLastFocusX = focusX;
				mDownFocusY = mLastFocusY = focusY;
				if (count == 3) {
					if (mListener != null) {
						// Log.i(TAG, "---mListener.onThreePointerTouch()---");
						mListener.onThreePointerTouch();
					}
					return true;
				}
				onActionPointerUp(ev, count);
				break;

			case MotionEvent.ACTION_DOWN:
				mDownFocusX = mLastFocusX = focusX;
				mDownFocusY = mLastFocusY = focusY;
				onActionDown(ev);
				break;

			case MotionEvent.ACTION_MOVE:
				onActionMove(ev, focusX, focusY);
				break;

			case MotionEvent.ACTION_UP:
				onActionUp(ev);
				mode = NONE;
				break;

			case MotionEvent.ACTION_CANCEL:
				cancel();
				break;
			}
			mImageView.setImageMatrix(mCurrentMatrix);
			CheckView();
			return true;
		}
	};

	private void onActionDown(MotionEvent ev) {
		mSavedMatrix.set(mCurrentMatrix);
		prev.set(ev.getX(), ev.getY());
		mode = DRAG;
		// gesture detector
		boolean hadTapMessage = mHandler.hasMessages(MSG_TAP);
		if (hadTapMessage)
			mHandler.removeMessages(MSG_TAP);
		if ((mCurrentDownEvent != null) && (mPreviousUpEvent != null) && hadTapMessage && isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent, ev)) {
			// This is a second tap
			mIsDoubleTapping = true;
			mHandler.sendEmptyMessage(MSG_DOUBLE_TAP);
		} else {
			// This is a first tap
			mHandler.sendEmptyMessageDelayed(MSG_TAP, DOUBLE_TAP_TIMEOUT);
		}

		if (mCurrentDownEvent != null) {
			mCurrentDownEvent.recycle();
		}
		mCurrentDownEvent = MotionEvent.obtain(ev);
		mAlwaysInTapRegion = true;
		mAlwaysInBiggerTapRegion = true;
		mStillDown = true;
		mInLongPress = false;

		if (mIsLongpressEnabled) {
			mHandler.removeMessages(MSG_LONG_PRESS);
			mHandler.sendEmptyMessageAtTime(MSG_LONG_PRESS, mCurrentDownEvent.getDownTime() + TAP_TIMEOUT + LONGPRESS_TIMEOUT);
		}
		mHandler.sendEmptyMessageAtTime(MSG_SHOW_PRESS, mCurrentDownEvent.getDownTime() + TAP_TIMEOUT);
	}

	private static final int SWIPE_MAX_OFF_PATH = 100;

	private static final int SWIPE_MIN_DISTANCE = 100;

	private void onActionUp(MotionEvent ev) {
		onHighlightHide(OnZoomGestureListener.MSG_HIDE_DELAYED);
		// gesture detector
		mStillDown = false;
		MotionEvent currentUpEvent = MotionEvent.obtain(ev);
		if (mIsDoubleTapping) {
			// Finally, give the up event of the double-tap
		} else if (mInLongPress) {
			mHandler.removeMessages(MSG_TAP);
			mInLongPress = false;
		} else if (mAlwaysInTapRegion) {
			// handled = mListener.onSingleTapUp(ev);
		} else {
			// A fling must travel the minimum tap distance
			final VelocityTracker velocityTracker = mVelocityTracker;
			final int pointerId = ev.getPointerId(0);
			velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
			final float velocityY = velocityTracker.getYVelocity(pointerId);
			final float velocityX = velocityTracker.getXVelocity(pointerId);

			if ((Math.abs(velocityY) > mMinimumFlingVelocity) || (Math.abs(velocityX) > mMinimumFlingVelocity)) {
				if (mode == DRAG && mListener != null) {
					float space_x = ev.getX() - prev.x;
					float space_y = ev.getY() - prev.y;
					if (Math.abs(space_y) < SWIPE_MAX_OFF_PATH && !hasScaled(mCurrentMatrix)) {
						if (space_x > SWIPE_MIN_DISTANCE) {
							mListener.onEventDetected(OnZoomGestureListener.EVENT_FLINGRIGHT);
						} else if (space_x < -SWIPE_MIN_DISTANCE) {
							mListener.onEventDetected(OnZoomGestureListener.EVENT_FLINGLEFT);
						}
					}
				}
			}
		}
		if (mPreviousUpEvent != null) {
			mPreviousUpEvent.recycle();
		}
		// Hold the event we obtained above - listeners may have changed the
		// original.
		mPreviousUpEvent = currentUpEvent;
		if (mVelocityTracker != null) {
			// This may have been cleared when we called out to the
			// application above.
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
		mIsDoubleTapping = false;
		mHandler.removeMessages(MSG_SHOW_PRESS);
		mHandler.removeMessages(MSG_LONG_PRESS);
	}

	private void onActionMove(MotionEvent ev, float focusX, float focusY) {
		if (mode == DRAG && hasScaled(mCurrentMatrix)) {
			mCurrentMatrix.set(mSavedMatrix);
			mCurrentMatrix.postTranslate(ev.getX() - prev.x, ev.getY() - prev.y);
			mMaxMatrix.set(mCurrentMatrix);
			onHighlightShow(true);
		} else if (mode == ZOOM) {
			if (!mCanZoom) {
				return;
			}
			float newDist = spacing(ev);
			if (newDist > DOUBLE_POINT_DISTANCE) {
				mCurrentMatrix.set(mSavedMatrix);
				float tScale = newDist / dist;
				mCurrentMatrix.postScale(tScale, tScale, mid.x, mid.y);
				onHighlightShow(false);

				// 用于储存最大缩放值及对应的移位
				float p[] = new float[9];
				mCurrentMatrix.getValues(p);
				if (p[0] >= MAX_SCALE || p[4] >= MAX_SCALE) {
					float m[] = new float[9];
					mMaxMatrix.getValues(m);
					if (m[0] < MAX_SCALE && m[4] < MAX_SCALE) {
						Log.i(TAG, "pre got it.........." + Arrays.toString(p));
						p[2] = MAX_SCALE * p[2] / p[0];
						p[5] = MAX_SCALE * p[5] / p[0];
						p[0] = MAX_SCALE;
						p[4] = MAX_SCALE;
						mMaxMatrix.setValues(p);
						Log.i(TAG, "got it.........." + Arrays.toString(p));
					}
				}
			}
		}
		// gesture detector
		if (mInLongPress) {
			return;
		}
		final float scrollX = mLastFocusX - focusX;
		final float scrollY = mLastFocusY - focusY;
		if (mIsDoubleTapping) {
			// Give the move events of the double-tap
			// handled |= mDoubleTapListener.onDoubleTapEvent(ev);
		} else if (mAlwaysInTapRegion) {
			final int deltaX = (int) (focusX - mDownFocusX);
			final int deltaY = (int) (focusY - mDownFocusY);
			int distance = (deltaX * deltaX) + (deltaY * deltaY);
			if (distance > mTouchSlopSquare) {
				// handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX,
				// scrollY);
				mLastFocusX = focusX;
				mLastFocusY = focusY;
				mAlwaysInTapRegion = false;
				mHandler.removeMessages(MSG_TAP);
				mHandler.removeMessages(MSG_SHOW_PRESS);
				mHandler.removeMessages(MSG_LONG_PRESS);
			}
		} else if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {
			// handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX,
			// scrollY);
			mLastFocusX = focusX;
			mLastFocusY = focusY;
		}
	}

	private void onActionPointerDown(MotionEvent ev) {
		dist = spacing(ev);
		checkExitState(true, dist);
		// 如果连续两点距离大于10，则判定为多点模式
		if (dist > DOUBLE_POINT_DISTANCE) {
			mSavedMatrix.set(mCurrentMatrix);
			midPoint(mid, ev);
			mode = ZOOM;
		}
		// Cancel long press and taps
		cancelTaps();
	}

	private void onActionPointerUp(MotionEvent ev, int count) {
		onHighlightShow(false);
		Log.i(TAG, "up...");
		mode = NONE;
		// gesture detector
		// Check the dot product of current velocities.
		// If the pointer that left was opposing another velocity vector,
		// clear.
		mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
		final int upIndex = ev.getActionIndex();
		// Log.i(TAG, "当前按起的getActionIndex是"+upIndex);
		final int id1 = ev.getPointerId(upIndex);
		// Log.i(TAG, "当前getPointerId是"+id1);
		final float x1 = mVelocityTracker.getXVelocity(id1);
		final float y1 = mVelocityTracker.getYVelocity(id1);
		for (int i = 0; i < count; i++) {
			if (i == upIndex)
				continue;

			final int id2 = ev.getPointerId(i);
			final float x = x1 * mVelocityTracker.getXVelocity(id2);
			final float y = y1 * mVelocityTracker.getYVelocity(id2);

			final float dot = x + y;
			if (dot < 0) {
				mVelocityTracker.clear();
				break;
			}
		}
		checkExitState(false, spacing(ev));
	}

	private void cancel() {
		mHandler.removeMessages(MSG_TAP);
		mHandler.removeMessages(MSG_SHOW_PRESS);
		mHandler.removeMessages(MSG_LONG_PRESS);
		mVelocityTracker.recycle();
		mVelocityTracker = null;
		mIsDoubleTapping = false;
		mStillDown = false;
		mAlwaysInTapRegion = false;
		mAlwaysInBiggerTapRegion = false;
		if (mInLongPress) {
			mInLongPress = false;
		}
	}

	private void cancelTaps() {
		mHandler.removeMessages(MSG_TAP);
		mHandler.removeMessages(MSG_SHOW_PRESS);
		mHandler.removeMessages(MSG_LONG_PRESS);
		mIsDoubleTapping = false;
		mAlwaysInTapRegion = false;
		mAlwaysInBiggerTapRegion = false;
		if (mInLongPress) {
			mInLongPress = false;
		}
	}

	private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
		if (!mAlwaysInBiggerTapRegion) {
			return false;
		}

		if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
			return false;
		}

		int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
		int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
		return (deltaX * deltaX + deltaY * deltaY < mDoubleTapSlopSquare);
	}

	private void dispatchLongPress() {
		mHandler.removeMessages(MSG_TAP);
		mInLongPress = true;
		if (mListener != null) {
			mListener.onLongPress(mCurrentDownEvent);
		}

	}

	/**
	 * 显示高亮图层
	 */
	private void onHighlightShow(boolean show) {
		if (mListener != null) {
			float p[] = new float[9];
			mCurrentMatrix.getValues(p);
			mListener.onHighlightShow(show, p[0], p[2], p[5]);
		}
	}

	/**
	 * 隐藏高亮图层
	 */
	private void onHighlightHide(int msg) {
		if (mListener != null) {
			mListener.onHighlightChanged(msg);
		}
	}

	private ImageView mImageView;
	private float mBitmapWidth, mBitmapHeight;
	private float mVisibleWidth, mVisibleHeight;// 可视区域宽度和高度

	Matrix mCurrentMatrix = new Matrix();
	Matrix mSavedMatrix = new Matrix();
	Matrix mMaxMatrix = new Matrix();// 最大缩放比例对应的矩阵
	/** 最小缩放比例 大图则缩小，小图则放大 宽/高适应 */
	// float mScaleMini;
	/** 填充满时的x/y对应的缩放比例 */
	float mScaleX, mScaleY;
	/** 是否可以进行缩放 */
	boolean mCanZoom = false;

	static final int DOUBLE_POINT_DISTANCE = 10; // 两点放大两点间最小间距
	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	float mExitSpace;

	/** 最大缩放比例 **/
	static float MAX_SCALE = 2.0f;// 最大缩放比例

	/**
	 * 手势事件
	 * 
	 * @author Michael.li
	 * 
	 */
	public interface OnZoomGestureListener {
		/** 最小化后再次缩小事件 */
		public static final int EVENT_EXIT = 0;
		/** 单击事件 */
		public static final int EVENT_TAP = 1;
		/** 左向滑动事件 */
		public static final int EVENT_FLINGLEFT = 2;
		/** 右向滑动事件 */
		public static final int EVENT_FLINGRIGHT = 3;

		void onEventDetected(int event);

		void onLongPress(MotionEvent mCurrentDownEvent);

		/**
		 * 显示高亮图层
		 * 
		 * @param show
		 *            是否控制显示
		 * 
		 * @param scale
		 *            缩放比例
		 * @param x
		 *            横轴偏移
		 * @param y
		 *            纵轴偏移
		 */
		void onHighlightShow(boolean show, float scale, float x, float y);

		/** 立即隐藏 */
		public static final int MSG_HIDE = 0;
		/** 延迟隐藏 */
		public static final int MSG_HIDE_DELAYED = 1;

		/**
		 * 高亮图层状态改变
		 */
		void onHighlightChanged(int msg);

		/**
		 * 三点触摸操作
		 */
		public void onThreePointerTouch();
	}

	private OnZoomGestureListener mListener;// 手势监听事件

	public void setOnZoomGestureListener(OnZoomGestureListener listener) {
		this.mListener = listener;
	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void CheckView() {
		if (mode != ZOOM) {
			center();
			return;
		}
		if (!mCanZoom) {
			return;
		}
		float p[] = new float[9];
		mCurrentMatrix.getValues(p);
		if (p[0] < mScaleX || p[4] < mScaleY) {
			// matrix.setScale(mScaleMini, mScaleMini);
			mCurrentMatrix.setScale(mScaleX, mScaleY);
		}
		if (p[0] > MAX_SCALE || p[4] > MAX_SCALE) {
			// matrix.set(savedMatrix); //恢复到上次缩放的大小
			mMaxMatrix.getValues(p);
			// if(p[0] < mScaleX || p[4] < mScaleY)
			// System.out.println("ddd:" + Arrays.toString(p));
			// 小图放大后适应全屏
			mCurrentMatrix.set(mMaxMatrix); // 最大缩放比例
		}
		center();
	}

	/**
	 * 检测退出指令是否应该发送
	 * 
	 * @param down
	 *            触点按下/离开
	 * @param dist
	 *            两指间的距离
	 */
	private void checkExitState(boolean down, float dist) {
		if (!down) {
			if (mExitSpace - dist > 100) {
				mHandler.sendEmptyMessageDelayed(MSG_ZOOM_EXIT, 100);
			}
			return;
		}
		if (!hasScaled(mSavedMatrix)) {
			mExitSpace = dist;
		}
	}

	/**
	 * 是否与满屏时的缩放比例不同
	 * 
	 * @param matrix
	 * @return
	 */
	private boolean hasScaled(Matrix matrix) {
		float p[] = new float[9];
		matrix.getValues(p);
		Log.i(TAG, " matrix:" + Arrays.toString(p) + " mScaleX:" + mScaleX + " mScaleY:" + mScaleY);
		return p[0] != mScaleX && p[4] != mScaleY;
	}

	/**
	 * 计算最小缩放比例
	 */
	private void minZoom() {
		mScaleX = (float) mVisibleWidth / mBitmapWidth;
		mScaleY = (float) mVisibleHeight / mBitmapHeight;

		mCurrentMatrix.postScale(mScaleX, mScaleY);
		if (mScaleX < MAX_SCALE && mScaleY < MAX_SCALE) {
			mCanZoom = true;
		}
		// mMaxMatrix = mCurrentMatrix;
		// mScaleMini = Math.min(mScaleX, mScaleY);
		// mCurrentMatrix.postScale(mScaleMini, mScaleMini);// 宽/高适应
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 * 
	 * @param horizontal
	 * @param vertical
	 */
	private void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(mCurrentMatrix);
		RectF rect = new RectF(0, 0, mBitmapWidth, mBitmapHeight);
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			if (height < mVisibleHeight) {
				deltaY = (mVisibleHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < mVisibleHeight) {
				deltaY = mImageView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			if (width < mVisibleWidth) {
				deltaX = (mVisibleWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < mVisibleWidth) {
				deltaX = mVisibleWidth - rect.right;
			}
		}
		mCurrentMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 计算两点的距离
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 计算两点的之间的中间点
	 * 
	 * @param point
	 * @param event
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
