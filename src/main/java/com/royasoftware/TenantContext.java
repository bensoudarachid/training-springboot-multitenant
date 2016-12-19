package com.royasoftware;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.royasoftware.settings.security.CustomUserDetails;

public class TenantContext {
	private static Logger logger = LoggerFactory.getLogger(TenantContext.class);
//    private static ThreadLocal<Map<String,Object>> currentTenant = new ThreadLocal<>();
	public static String userUploadStorage="../useruploadstorage/";
	private static Set<String> validTenantSet= new HashSet<>();
    private static ThreadLocal<HashMap<String, Object>> myContextThreadLocal = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<>();
        }
    };
    public static void setCurrentTenant(String tenant) throws Exception{
    	if( !isTenantValid(tenant))
    		throw new Exception("Subdomain not valid");
        myContextThreadLocal.get().put("tenant",tenant);
    }
    public static String getCurrentTenant() {
        return (String)myContextThreadLocal.get().get("tenant");
    }
    public static String getCurrentTenantStoragePath() {
    	return new StringBuffer(userUploadStorage).append(myContextThreadLocal.get().get("tenant")).append("/").toString();
//        return userUploadStorage+(String)myContextThreadLocal.get().get("tenant")+"/";
    }
    private static String getCurrentTenantStoragePath(String tenant) {
    	return new StringBuffer(userUploadStorage).append(tenant).append("/").toString();
//        return userUploadStorage+(String)myContextThreadLocal.get().get("tenant")+"/";
    }
    public static void setCurrentUser(CustomUserDetails user) {
        myContextThreadLocal.get().put("activeuser",user);
    }
    public static CustomUserDetails getCurrentUser() {
    	return (CustomUserDetails)myContextThreadLocal.get().get("activeuser");
    }
    public static void setValidTenants(Set validTenants){
//    	validTenantSet =  (Collection<String>)(Collection<?>)validTenants;
    	Set<String> validTenantSet = new HashSet<>();
        for (Object tenant : validTenants) {
        	addValidTenant((String) tenant);
        }
    }
    public static void addValidTenant(String tenant){
    	logger.info("add valid tenant :"+tenant);
    	validTenantSet.add(tenant);
    	new File(getCurrentTenantStoragePath(tenant)).mkdirs();    	
    }
    private static boolean isTenantValid(String tenant){
    	return validTenantSet.contains(tenant);
    }
}
