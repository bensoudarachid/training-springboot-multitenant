package com.royasoftware.service;

import java.io.Serializable;
import java.util.HashMap;

import com.royasoftware.TenantContext;

public interface AkkaAppMsg extends Serializable{
	HashMap<String, Object> originalThreadHashMap = TenantContext.getTenantContextThreadLocal().get();
	public default HashMap<String, Object> getOriginalThreadHashMap() {
		return originalThreadHashMap;
	}
}