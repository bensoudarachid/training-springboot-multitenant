package com.royasoftware.settings.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class Auth2FilterRequest implements Filter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//		logger.info("Calling auth2 filter");

	    try {
	        HttpServletRequest request = (HttpServletRequest) req;
	        HttpServletResponse response = (HttpServletResponse) res;

            chain.doFilter(req, res);
	    } catch (Exception exception) {
	    } finally {
	    }
	}
    @Override
    public void init(FilterConfig filterConfig) {
    }
    @Override
    public void destroy() {
    }

}
