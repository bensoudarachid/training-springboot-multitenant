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
	public static String userUploadStorage=System.getenv("TRAINING_APP_STORAGE");
	private static Set<String> validTenantSet= new HashSet<>();
    private static ThreadLocal<HashMap<String, Object>> myContextThreadLocal = new ThreadLocal<HashMap<String, Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<>();
        }
    };
    public static void resetThreadLocal(){
    	myContextThreadLocal.remove();
    }
    public static void setCurrentTenant(String tenant) throws Exception{
//    	logger.info("set current tenant "+tenant);
    	if( tenant==null)
    		myContextThreadLocal.get().put("tenant",tenant);
//    	logger.info("set valid current tenant "+tenant);
    	if( !isTenantValid(tenant)){
    		myContextThreadLocal.get().put("tenant",null);
    		throw new Exception("Subdomain "+tenant+" not valid");
    	}
//    	logger.info("set valid current tenant "+tenant);
        myContextThreadLocal.get().put("tenant",tenant);
    }
    public static String getCurrentTenant() {
//    	logger.info("get current tenant "+myContextThreadLocal.get().get("tenant"));
        return (String)myContextThreadLocal.get().get("tenant");
    }
    public static String getCurrentTenantStoragePath() {
    	return new StringBuffer(userUploadStorage).append(myContextThreadLocal.get().get("tenant")).append("/").toString();
//        return userUploadStorage+(String)myContextThreadLocal.get().get("tenant")+"/";
    }
    public static String getCurrentTenantStoragePath(String subfolder) {
    	return new StringBuffer(userUploadStorage).append(myContextThreadLocal.get().get("tenant")).append("/").append(subfolder).append("/").toString();
    }
    public static String getCurrentUserStoragePath(String name) {
    	CustomUserDetails user = getCurrentUser();
    	if( user == null )
    		return null;
//    	logger.info("myContextThreadLocal.get().get(tenant)="+myContextThreadLocal.get().get("tenant"));
//    	return getCurrentTenantStoragePath();
    	return new StringBuffer(userUploadStorage).append(myContextThreadLocal.get().get("tenant")).append("/user/").append(user.getId()).append("/").append(name).append("/").toString();
//        return userUploadStorage+(String)myContextThreadLocal.get().get("tenant")+"/";
    }
    private static String getTenantStoragePath(String tenant) {
    	logger.info("userUploadStorage="+userUploadStorage); 
    	return new StringBuffer(userUploadStorage).append(tenant).append("/").toString();
//        return userUploadStorage+(String)myContextThreadLocal.get().get("tenant")+"/";
    }
    public static void setCurrentUser(CustomUserDetails user) {
//    	logger.info("setCurrentUser user="+user.getUsername()); 
        myContextThreadLocal.get().put("activeuser",user);
    }
    public static CustomUserDetails getCurrentUser() {
    	return (CustomUserDetails)myContextThreadLocal.get().get("activeuser");
    }
    public static void setValidTenants(Set validTenants){
//    	validTenantSet =  (Collection<String>)(Collection<?>)validTenants;
//    	logger.info("--------> RUN AUTOIT F10");
//    	ScriptHelper.run(ScriptHelper.RUN_WEB_APP);
    	Set<String> validTenantSet = new HashSet<>();
        for (Object tenant : validTenants) {
        	addValidTenant((String) tenant);
        }
    }
    public static void addValidTenant(String tenant){
    	logger.info("add valid tenant :"+tenant);
    	validTenantSet.add(tenant);
    	new File(getTenantStoragePath(tenant)).mkdirs();    	
    }
    private static boolean isTenantValid(String tenant){
    	return validTenantSet.contains(tenant);
    }
}
