package com.royasoftware.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.net.InternetDomainName;
import com.royasoftware.TenantContext;
import com.royasoftware.settings.security.CustomUserDetails;

@Component
public class AppControllerInterceptor extends HandlerInterceptorAdapter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
    	
    	Authentication authentication =
    			SecurityContextHolder.getContext().getAuthentication();
    	if(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))
    		TenantContext.setCurrentUser(null);
    	else{
	    	CustomUserDetails activeUser = (CustomUserDetails) (authentication == null ? null : authentication.getPrincipal());
	//    	logger.info("AppControllerInterceptor. User name = " + activeUser.getUsername() + ", id = "
	//				+ activeUser.getId() + ", role = " + activeUser.getAuthorities().toString());
	    	TenantContext.setCurrentUser(activeUser);
    	}
		String site = request.getServerName();
		String domain = null;
		if( !site.endsWith(".localhost"))
//			domain = InternetDomainName.from(request.getServerName()).topPrivateDomain().toString();
			domain = site.substring(site.lastIndexOf('.', site.lastIndexOf('.')-1) + 1);
		else
			domain="localhost";
        String subdomain = site.replaceAll(domain, "");
        subdomain = subdomain.substring(0, subdomain.length() - 1);
        if(subdomain.contains("."))
        	throw new Exception("Sub with point is not allowed");
        
//		TenantContext.setCurrentTenant(subdomain);
		TenantContext.setCurrentTenant("abbaslearning");
    	logger.info("AppControllerInterceptor. Set tenant context subdomain: "+subdomain);
        // set few parameters to handle ajax request from different host
        return super.preHandle(request, response, handler);
    }

//    @Override
//    public void postHandle(HttpServletRequest request,
//            HttpServletResponse response, Object handler,
//            ModelAndView modelAndView) throws Exception {
//    	logger.info("**********************PostHande this");
//        super.postHandle(request, response, handler, modelAndView);
//    }
//	protected String getSubdomain(HttpServletRequest request) throws Exception{
//		String site = request.getServerName();
//        String domain = InternetDomainName.from(request.getServerName()).topPrivateDomain().toString();
//        String subdomain = site.replaceAll(domain, "");
//        subdomain = subdomain.substring(0, subdomain.length() - 1);
//        logger.info("Base controller. Subdomain = " + subdomain);
//        if(subdomain.contains("."))
//        	throw new Exception("Mammaaaaa! Sub with point is not allowed");
//		TenantContext.setCurrentTenant(subdomain);
//      return subdomain;
//	}

}