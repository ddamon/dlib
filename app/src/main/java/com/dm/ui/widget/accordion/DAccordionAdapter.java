package com.dm.ui.widget.accordion;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 适配器
 * 
 * 
 */
public abstract class DAccordionAdapter<T> {
	protected List<T> list;
	protected Context context;

	public DAccordionAdapter(Context context, List<T> list) {
		this.context = context;
		this.list = list;
	}

	/**
	 * 手风琴的项目个数
	 * 
	 * @return
	 */
	public int getCount() {
		return list.size();
	}

	public T getItem(int position) {
		return list.get(position);
	}

	/**
	 * 是否可见
	 * 
	 * @param position
	 * @return
	 */
	public int getVisibility(int position) {
		return View.VISIBLE;
	}

	/**
	 * 获得标题栏的view
	 * 
	 * @param position
	 * @param parent
	 * @return
	 */
	public abstract View getHeaderView(int position, ViewGroup parent);

	/**
	 * 获得内容区的view
	 * 
	 * @param position
	 * @param parent
	 * @return
	 */
	public abstract View getContentView(int position, ViewGroup parent);

	/**
	 * @param position
	 * @param headerView
	 */
	public void onExpanded(int position, View headerView) {
		ViewGroup currentView = (ViewGroup) headerView.getParent();
		currentView.getChildAt(1).setVisibility(View.VISIBLE);
	}

	/**
	 * @param position
	 * @param headerView
	 */
	public void onCollapsed(int position, View headerView) {
		ViewGroup currentView = (ViewGroup) headerView.getParent();
		currentView.getChildAt(1).setVisibility(View.GONE);
	}
}
