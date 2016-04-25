package com.dm.ui.fragment;

import java.io.Serializable;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.dm.GlobalContext;
import com.dm.dlib.R;
import com.dm.ioc.InjectUtil;
import com.dm.ioc.anno.InjectView;
import com.dm.ui.activity.BaseActivity;
import com.dm.utils.UIHelper;

public abstract class BaseFragment extends Fragment {
	ViewGroup rootView;// 根视图
	@InjectView(idStr = "layoutLoading")
	View loadingLayout;// 加载中视图
	@InjectView(idStr = "layoutLoadFailed")
	View loadFailureLayout;// 加载失败视图
	@InjectView(idStr = "layoutContent")
	View contentLayout;// 内容视图
	@InjectView(idStr = "layoutEmpty")
	View emptyLayout;// 空视图

	static final String TAG = "BaseFragment";

	protected enum ABaseTaskState {
		none, prepare, falid, success, finished, canceled
	}

	// 标志是否ContentView是否为空
	private boolean contentEmpty = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof BaseActivity)
			((BaseActivity) activity).addFragment(toString(), this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (inflateContentView() > 0) {
			rootView = (ViewGroup) inflater.inflate(inflateContentView(), null);
			rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			_layoutInit(inflater, savedInstanceState);
			layoutInit(inflater, savedInstanceState);
			return rootView;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		InjectUtil.inject(this);
		if (savedInstanceState == null)
			requestData();

	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (getActivity() != null && getActivity() instanceof BaseActivity)
			((BaseActivity) getActivity()).removeFragment(this.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 根视图
	 * 
	 * @return
	 */
	public ViewGroup getRootView() {
		return rootView;
	}

	/**
	 * Action的home被点击了
	 * 
	 * @return
	 */
	public boolean onHomeClick() {
		return onBackClick();
	}

	/**
	 * 返回按键被点击了
	 * 
	 * @return
	 */
	public boolean onBackClick() {
		return false;
	}

	/**
	 * 初次创建时默认会调用一次
	 */
	public void requestData() {

	}

	/**
	 * A*Fragment重写这个方法
	 * 
	 * @param inflater
	 * @param savedInstanceSate
	 */
	void _layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
		if (emptyLayout != null) {
			View reloadView = emptyLayout.findViewById(R.id.layoutReload);
			if (reloadView != null)
				setOnClickListener(reloadView, null);
		}

		if (loadFailureLayout != null) {
			View reloadView = loadFailureLayout.findViewById(R.id.layoutReload);
			if (reloadView != null)
				setOnClickListener(reloadView, null);
		}

		setViewVisiable(loadingLayout, View.GONE);
		setViewVisiable(loadFailureLayout, View.GONE);
		setViewVisiable(emptyLayout, View.GONE);
		if (isContentEmpty()) {
			if (savedInstanceSate != null) {
				requestData();
			} else {
				setViewVisiable(emptyLayout, View.VISIBLE);
				setViewVisiable(contentLayout, View.GONE);
			}
		} else {
			setViewVisiable(contentLayout, View.VISIBLE);
		}
	}

	/**
	 * 子类重写这个方法，初始化视图
	 * 
	 * @param inflater
	 * @param savedInstanceSate
	 */
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {

	}

	protected View findViewById(int viewId) {
		if (rootView == null)
			return null;
		return rootView.findViewById(viewId);
	}

	abstract protected int inflateContentView();

	protected void setOnClickListener(View v, Object tag) {
		if (v == null)
			return;
		v.setTag(tag);
		v.setOnClickListener(viewOnClickListener);
	}

	protected void setOnClickListener(int viewId, Object tag) {
		setOnClickListener(findViewById(viewId), tag);
	}

	OnClickListener viewOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onLoadViewClicked(v);
		}
	};

	public void setContentEmpty(boolean empty) {
		this.contentEmpty = empty;
	}

	public boolean isContentEmpty() {
		return contentEmpty;
	}

	/**
	 * 视图点击回调，子类重写
	 * 
	 * @param view
	 */
	public void onLoadViewClicked(View view) {
		if (view.getId() == R.id.layoutReload) {
			requestData();
		} else if (view.getId() == R.id.layoutRefresh) {
			requestData();
		}
	}

	void setViewVisiable(View v, int visibility) {
		if (v != null)
			v.setVisibility(visibility);
	}

	/**
	 * 根据{@link ABaseTask} 的加载状态，刷新视图
	 * 
	 * @param state
	 * @param obj
	 *            附带辅助的数据，根据状态定义
	 */
	protected void taskStateChanged(ABaseTaskState state, Serializable tag) {
		// 开始Task
		if (state == ABaseTaskState.prepare) {
			if (isContentEmpty()) {
				setViewVisiable(loadingLayout, View.VISIBLE);
				setViewVisiable(contentLayout, View.GONE);
			} else {
				setViewVisiable(loadingLayout, View.GONE);
				setViewVisiable(contentLayout, View.VISIBLE);
			}
			setViewVisiable(emptyLayout, View.GONE);
			setViewVisiable(loadFailureLayout, View.GONE);
		}
		// Task成功
		else if (state == ABaseTaskState.success) {
			setViewVisiable(loadingLayout, View.GONE);

			if (isContentEmpty()) {
				setViewVisiable(emptyLayout, View.VISIBLE);
			} else {
				setViewVisiable(contentLayout, View.VISIBLE);
			}
		}
		// 取消Task
		else if (state == ABaseTaskState.canceled) {
			if (isContentEmpty()) {
				setViewVisiable(loadingLayout, View.GONE);
				setViewVisiable(emptyLayout, View.VISIBLE);
			}
		}
		// Task失败
		else if (state == ABaseTaskState.falid) {
			if (isContentEmpty()) {
				setViewVisiable(emptyLayout, View.GONE);
				setViewVisiable(loadingLayout, View.GONE);
				setViewVisiable(loadFailureLayout, View.VISIBLE);
				if (tag != null && loadFailureLayout != null)
					((TextView) loadFailureLayout.findViewById(R.id.txtLoadFailed)).setText(tag.toString());
			}
		}
		// Task结束
		else if (state == ABaseTaskState.finished) {

		}
	}

	/**
	 * 以Toast形式显示一个消息
	 * 
	 * @param msg
	 */
	protected void showMessage(CharSequence msg) {
		if (!TextUtils.isEmpty(msg))
			UIHelper.makeToast(GlobalContext.getInstance(), msg.toString());
	}

	/**
	 * 参照{@linkplain #showMessage(String)}
	 * 
	 * @param msgId
	 */
	protected void showMessage(int msgId) {
		if (getActivity() != null)
			showMessage(getString(msgId));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * 列表控件
	 * 
	 * @return
	 */
	abstract public AbsListView getRefreshView();

}
