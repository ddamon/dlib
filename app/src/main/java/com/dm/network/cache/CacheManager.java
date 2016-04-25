package com.dm.network.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.dm.db.DB;
import com.dm.db.SqlProxy;
import com.dm.ioc.IocContainer;
import com.dm.utils.MD5;

/**
 * 网络缓存管理
 * 
 * 
 * 
 */
public class CacheManager {

	DB db;

	public CacheManager() {
		super();
		db = IocContainer.getIocContainer().get(DB.class);
	}

	/**
	 * 创建缓存
	 * 
	 * @param url
	 * @param params
	 * @param result
	 */
	public void creata(String url, Map<String, Object> params, String result) {
		delete(url, params);
		Cache cache = new Cache();
		cache.setKey(buildKey(url, params));
		cache.setResult(result);
		cache.setUpdateTime(System.currentTimeMillis());
		if (cache != null) {
			db.save(cache);
		}
	}

	/**
	 * 获取缓存
	 * 
	 * @param url
	 * @param params
	 */
	public String get(String url, Map<String, Object> params) {
		Cache cache = db.queryFrist(Cache.class, "key = ?", buildKey(url, params));
		if (cache != null) {
			return cache.getResult();
		}
		return null;
	}

	/**
	 * 删除缓存
	 * 
	 * @param url
	 * @param params
	 */
	public void delete(String url, Map<String, Object> params) {
		db.execProxy(SqlProxy.delete(Cache.class, "key = ?", buildKey(url, params)));
	}

	/**
	 * 删除多少天前的缓存
	 * 
	 * @param dayAgo
	 */
	public void deleteByDate(Integer dayAgo) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -dayAgo);
		Date time = calendar.getTime();
		db.execProxy(SqlProxy.delete(Cache.class, "key < ?", time.getTime()));
	}

	private String buildKey(String url, Map<String, Object> params) {
		if (params != null) {
			url += params.toString();
		}
		try {
			return MD5.encryptMD5(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

}
