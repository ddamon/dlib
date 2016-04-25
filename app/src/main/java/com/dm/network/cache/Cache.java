package com.dm.network.cache;

import com.dm.db.Column;

/**
 * 缓存对象
 * 
 * 
 * 
 */
public class Cache {

	@Column(pk = true)
	public Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String key;
	public String result;
	public Long updateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
