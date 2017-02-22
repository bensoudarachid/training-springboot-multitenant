package com.royasoftware.settings.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.royasoftware.TenantContext;
import com.royasoftware.settings.security.CustomUserDetails;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsAndMultitenantDomainFilterRequest implements Filter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Logger log = LoggerFactory.getLogger(CorsAndMultitenantDomainFilterRequest.class);

    public CorsAndMultitenantDomainFilterRequest() {
        log.info("SimpleCORSFilter init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	logger.info("CorsAndMultitenantDomainFilterRequest. Call ");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, HEAD");
        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Authorization, Content-Type");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, cache-control, authentication, authorization, Content-Type, Origin, X-Auth-Token, client-security-token");
        response.setHeader("Access-Control-Max-Age", "3600");
        

        
		String site = request.getServerName();
		String domain = null;
		if( !site.endsWith(".localhost"))
//			domain = InternetDomainName.from(request.getServerName()).topPrivateDomain().toString();
			domain = site.substring(site.lastIndexOf('.', site.lastIndexOf('.')-1) + 1);
		else
			domain="localhost";
        try {
			String subdomain = site.replaceAll(domain, "");
			subdomain = subdomain.substring(0, subdomain.length() - 1);
			if(subdomain.contains("."))
				throw new Exception("Sub with point is not allowed");
			
			TenantContext.setCurrentTenant(subdomain);
//		TenantContext.setCurrentTenant("abbaslearning");
			logger.info("CorsAndMultitenantDomainFilterRequest. Set tenant context subdomain: "+subdomain);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//        	logger.warn("+++++++++++Preflight OPTIONS responding"+HttpServletResponse.SC_OK);
            response.setStatus(HttpServletResponse.SC_OK);             
//            response.setStatus(HttpStatus.OK.value());
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
