package com.dm.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 
 * 全局的网络数据修复 应用中基本需要实现的
 *
 */
public interface ValueFix {

	public Object fix(Object o, String type);

	public DisplayImageOptions imageOptions(String type);

}
