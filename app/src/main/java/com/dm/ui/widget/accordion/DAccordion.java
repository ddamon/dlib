package com.dm.ui.widget.accordion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 手风琴效果
 */
public class DAccordion extends LinearLayout {
	private int mCurrent = -1;// 当前展开的索引
	private DAccordionAdapter<?> mAdapter;

	public DAccordion(Context context) {
		this(context, null);
	}

	public DAccordion(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DAccordion(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
	}

	/**
	 * 
	 * @param adapter
	 * @param expand
	 *            expandPosition 展开的索引 从0开始
	 */
	public void setAdapter(DAccordionAdapter<?> adapter, int expandPosition) {
		removeAllViews();
		mAdapter = adapter;
		for (int i = 0, j = adapter.getCount(); i < j; i++) {
			LinearLayout wrapper = getWrapper(i == expandPosition);
			View header = adapter.getHeaderView(i, wrapper);
			View content = adapter.getContentView(i, wrapper);
			header.setTag(i);
			content.setVisibility(View.GONE);
			content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			wrapper.addView(header);
			wrapper.addView(content);
			header.setOnClickListener(clickListener);
			wrapper.setVisibility(adapter.getVisibility(i));
			addView(wrapper);
		}
		expand(expandPosition);
	}

	public DAccordionAdapter<?> getAdapter() {
		return mAdapter;
	}

	/**
	 * 展开第几个
	 * 
	 * @param position
	 */
	public void expand(int position) {
		if (mCurrent >= 0) {
			LinearLayout currentWrapper = (LinearLayout) getChildAt(mCurrent);
			setWrapperLayoutParams(currentWrapper, false);
			mAdapter.onCollapsed(mCurrent, currentWrapper.getChildAt(0));
		}
		mCurrent = position;
		LinearLayout focusedWrapper = (LinearLayout) getChildAt(mCurrent);
		if (focusedWrapper != null) {
			setWrapperLayoutParams(focusedWrapper, true);
			mAdapter.onExpanded(mCurrent, focusedWrapper.getChildAt(0));
		}
	}

	/**
	 * 获得GroupView 如果不存在则返回null
	 * 
	 * @param group
	 *            索引 从0开始
	 * @return
	 */
	public View getGroupView(int group) {
		if (group < getChildCount()) {
			LinearLayout wrapper = (LinearLayout) getChildAt(group);
			return wrapper.getChildAt(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取第几组的头部view
	 * 
	 * @param group
	 * @return
	 */
	public ViewGroup getContentView(int group) {
		if (group < getChildCount()) {
			LinearLayout wrapper = (LinearLayout) getChildAt(group);
			return (ViewGroup) wrapper.getChildAt(1);
		} else {
			return null;
		}
	}

	/**
	 * 设置可见性
	 * 
	 * @param position
	 */
	public void setVisibility(int position, boolean visible) {
		LinearLayout wrapper = (LinearLayout) getChildAt(position);
		if (visible) {
			wrapper.setVisibility(View.VISIBLE);
		} else {
			wrapper.setVisibility(View.GONE);
		}
	}

	/**
	 * 当前展开的索引
	 * 
	 * @return
	 */
	public int getExpandPosition() {
		return mCurrent;
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = Integer.parseInt(v.getTag().toString());
			if (mCurrent == position) {
				return;
			} else {
				expand(position);
				// LinearLayout currentWrapper = (LinearLayout)
				// getChildAt(mCurrent);
				// LinearLayout focusedWrapper = (LinearLayout) v.getParent();
				//
				// setWrapperLayoutParams(currentWrapper, false);
				// setWrapperLayoutParams(focusedWrapper, true);
				//
				// mCurrent = position;
				// mAdapter.onCollapsed(currentWrapper.getChildAt(0));
				// mAdapter.onExpanded(v);
			}
		}
	};

	/**
	 * 获得Wrapper容器，用于添加header和content
	 * 
	 * @param isExpanded
	 *            是否展开
	 * @return
	 */
	private LinearLayout getWrapper(boolean isExpanded) {
		LinearLayout wrapper = new LinearLayout(getContext());
		setWrapperLayoutParams(wrapper, isExpanded);
		wrapper.setOrientation(LinearLayout.VERTICAL);
		return wrapper;
	}

	/**
	 * 设置LayoutParams
	 * 
	 * @param wrapper
	 * @param isExpanded
	 *            是否展开
	 */
	private void setWrapperLayoutParams(LinearLayout wrapper, boolean isExpanded) {
		LayoutParams params;
		if (isExpanded) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
			params.weight = 1;
		} else {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.weight = 0;
		}
		wrapper.setLayoutParams(params);
	}
}
