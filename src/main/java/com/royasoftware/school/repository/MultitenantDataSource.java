package com.royasoftware.school.repository;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.royasoftware.school.TenantContext;

public class MultitenantDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
//    	logger.info("MultitenantDataSource current tenant: "+TenantContext.getCurrentTenant());
        return TenantContext.getCurrentTenant();
    }
}
