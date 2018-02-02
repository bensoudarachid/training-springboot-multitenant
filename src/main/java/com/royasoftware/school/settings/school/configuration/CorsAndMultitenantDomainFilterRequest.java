package com.royasoftware.school.settings.school.configuration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.royasoftware.school.TenantContext;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsAndMultitenantDomainFilterRequest implements Filter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
    private final Logger log = LoggerFactory.getLogger(CorsAndMultitenantDomainFilterRequest.class);
    final Calendar calendar = Calendar.getInstance();
    
    public CorsAndMultitenantDomainFilterRequest() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, HEAD");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, cache-control, authentication, authorization, Content-Type, Origin, X-Auth-Token, client-security-token");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        String remoteHost = request.getHeader("ClientHost");
		String site = request.getServerName();
		if( remoteHost !=null )
			site = remoteHost.replace(".school.",".schoolapi."); 
		String domain = null;
		try {
			String[] parts = site.split("\\.");
			if( parts==null||parts.length!=4||!parts[3].equals("com")||!parts[2].equals("royasoftware")||!parts[1].equals("schoolapi") ){
				TenantContext.setCurrentTenant(null);
//				throw new Exception("Malformed URL");
			}else
				TenantContext.setCurrentTenant(parts[0]);

	        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
	            response.setStatus(HttpServletResponse.SC_OK);             
	        } else {
	            chain.doFilter(req, res);
	        }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			TenantContext.resetThreadLocal();
		}
       
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}