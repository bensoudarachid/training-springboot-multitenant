package com.royasoftware.school.repository;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import com.royasoftware.school.TenantContext;

public class CustomCacheResolver implements CacheResolver {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CacheManager cacheManager;

    public CustomCacheResolver(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
    	logger.info("resolve the right cache "+TenantContext.getCurrentTenant()); 
        Collection<Cache> caches = getCaches(cacheManager, context);
        Object cache = caches.iterator().next().getNativeCache();
        logger.info("found cache 1 ="+cache ); 
        return caches;
    }

    private Collection<Cache> getCaches(CacheManager cacheManager, CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames().stream()
//            .filter(cacheName -> {
//            	logger.info("filter cacheName = "+cacheName); 
//            	return !cacheName.equals("trainings");
//            })
            .map(cacheName -> {
            	logger.info("get cache by cacheName = "+cacheName);
            	return cacheManager.getCache(cacheName+"-"+TenantContext.getCurrentTenant());
            })
            .filter(cache -> cache != null)
            .collect(Collectors.toList());
    }
}