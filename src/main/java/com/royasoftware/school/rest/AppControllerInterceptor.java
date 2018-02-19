package com.royasoftware.school.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.net.InternetDomainName;
import com.royasoftware.school.TenantContext;
import com.royasoftware.school.settings.security.CustomUserDetails;

@Component
public class AppControllerInterceptor extends HandlerInterceptorAdapter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof String
				&& authentication.getPrincipal().equals("anonymousUser")) {
			TenantContext.setCurrentUser(null);
		} else {
			CustomUserDetails activeUser = (CustomUserDetails) (authentication == null ? null
					: authentication.getPrincipal());
			TenantContext.setCurrentUser(activeUser);
		}
		return super.preHandle(request, response, handler);
	}

}