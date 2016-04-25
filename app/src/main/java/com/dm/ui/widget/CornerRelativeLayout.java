package com.dm.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.dm.dlib.R;

/**
 * CornerRelativeLayout
 * 
 */
public class CornerRelativeLayout extends RelativeLayout {
	private int location;
	private int backgroundResId;
	private int focusBgResId;
	private boolean auto;

	public CornerRelativeLayout(Context context) {
		super(context);
	}

	public CornerRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CornerRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFromAttributes(context, attrs);
		setBackgroundResource(backgroundResId);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isClickable()) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// setBackgroundResource(focusBgResId);
				break;
			case MotionEvent.ACTION_UP:
				if (auto) {
					setBackgroundResource(backgroundResId);
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 还原背景色
	 */
	public void resetBackground() {
		setBackgroundResource(backgroundResId);
	}

	private void initFromAttributes(Context context, AttributeSet attrs) {
		TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.CornerLayout);
		location = types.getInteger(R.styleable.CornerLayout_location, 0);
		switch (location) {
		case 0:
			backgroundResId = R.drawable.shape_corner_single;
			focusBgResId = R.drawable.shape_focus_corner_single;
			break;
		case 1:
			backgroundResId = R.drawable.general_corner_top;
			focusBgResId = R.drawable.shape_focus_corner_top;
			break;
		case 2:
			backgroundResId = R.drawable.shape_corner_middle;
			focusBgResId = R.drawable.shape_focus_corner_middle;
			break;
		default:
			backgroundResId = R.drawable.general_corner_bottom;
			focusBgResId = R.drawable.shape_focus_corner_bottom;
			break;
		}
		backgroundResId = types.getResourceId(R.styleable.CornerLayout_bg, backgroundResId);
		focusBgResId = types.getResourceId(R.styleable.CornerLayout_focusBg, focusBgResId);
		auto = types.getBoolean(R.styleable.CornerLayout_auto, true);
		types.recycle();
	}
}
