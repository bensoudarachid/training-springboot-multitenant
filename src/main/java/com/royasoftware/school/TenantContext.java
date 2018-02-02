package com.royasoftware.school;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.royasoftware.school.settings.security.CustomUserDetails;

public class TenantContext {
	
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class);
	public static String userUploadStorage=System.getenv("TRAINING_APP_STORAGE");
	static{
		if( !userUploadStorage.endsWith(File.separator) )
			userUploadStorage=userUploadStorage+File.separator;
	}
	private static Set<String> validTenantSet= new HashSet<>();
    private static ThreadLocal<HashMap<String, Object>> tenantContextThreadLocal = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<>();
        }
    };
	public static ThreadLocal<HashMap<String, Object>> getTenantContextThreadLocal() {
		return tenantContextThreadLocal;
	}
    public static void setTenantContextThreadLocalMap(HashMap<String, Object> tenantContextThreadLocalMap) {
  	  ThreadLocal<HashMap<String, Object>> th = new ThreadLocal<HashMap<String, Object>>() {
	        @Override
	        protected HashMap<String, Object> initialValue() {
	            return new HashMap<>();
	        }
	    };
	    th.get().putAll(tenantContextThreadLocalMap);
		TenantContext.tenantContextThreadLocal = th;
	}
	public static void resetThreadLocal(){
    	tenantContextThreadLocal.remove();
    }
    public static void setCurrentTenant(String tenant) throws Exception{
    	if( tenant==null)
    		tenantContextThreadLocal.get().put("tenant",tenant);
    	if( !isTenantValid(tenant)){
    		tenantContextThreadLocal.get().put("tenant",null);
//    		throw new Exception("Subdomain "+tenant+" not valid");
    	}
        tenantContextThreadLocal.get().put("tenant",tenant);
    }
    public static String getCurrentTenant() {
        return (String)tenantContextThreadLocal.get().get("tenant");
    }
    public static String getCurrentTenantStoragePath() {
    	return new StringBuffer(userUploadStorage).append(tenantContextThreadLocal.get().get("tenant")).append("/").toString();
    }
    public static String getCurrentTenantStoragePath(String subfolder) {
    	return new StringBuffer(userUploadStorage).append(tenantContextThreadLocal.get().get("tenant")).append("/").append(subfolder).append("/").toString();
    }
    public static String getCurrentUserStoragePath(String name) {
    	CustomUserDetails user = getCurrentUser();
    	if( user == null )
    		return null;
    	return new StringBuffer(userUploadStorage).append(tenantContextThreadLocal.get().get("tenant")).append("/user/").append(user.getId()).append("/").append(name).append("/").toString();
    }
    public static String getTenantStoragePath(String tenant) {
    	return new StringBuffer(userUploadStorage).append(tenant).append("/").toString();
    }
    public static void setCurrentUser(CustomUserDetails user) {
        tenantContextThreadLocal.get().put("activeuser",user);
    }
    public static CustomUserDetails getCurrentUser() {
    	return (CustomUserDetails)tenantContextThreadLocal.get().get("activeuser");
    }
    public static void setValidTenants(Set validTenants){
    	Set<String> validTenantSet = new HashSet<>();
        for (Object tenant : validTenants) {
        	addValidTenant((String) tenant);
        }
    }
    public static void addValidTenant(String tenant){
    	validTenantSet.add(tenant);
    	new File(getTenantStoragePath(tenant)).mkdirs();    	
    }
    private static boolean isTenantValid(String tenant){
    	return validTenantSet.contains(tenant);
    }
}
