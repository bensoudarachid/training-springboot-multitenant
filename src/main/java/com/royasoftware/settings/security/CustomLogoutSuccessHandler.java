package com.royasoftware.settings.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security logout handler
 */
@Component
public class CustomLogoutSuccessHandler
        extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {

//	@Autowired
    private TokenStore tokenStore;
	
	private static final String BEARER_AUTHENTICATION = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "authorization";

    
    public CustomLogoutSuccessHandler() {
		super();
	}
    public CustomLogoutSuccessHandler(TokenStore tokenStore) {
		super();
		this.tokenStore = tokenStore;
	}

//    @Autowired
//    private JwtAccessTokenConverter jwtAccessTokenConverter;

//    Autowired
//    private TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        System.out.println("token 0 = "+token);
//        System.out.println("token 1 = "+token.split(" ")[1]);
        System.out.println("tokenStore = "+tokenStore);

        if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[1].trim());
            System.out.println("before removing oAuth2AccessToken = "+oAuth2AccessToken);
            if (oAuth2AccessToken != null) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
                oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[1].trim());
                System.out.println("after removing oAuth2AccessToken = "+oAuth2AccessToken);
            }
        }else{
            System.out.println("wrong token = "+token);
        }
//        if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
//            OAuth2RefreshToken oAuth2RefToken = tokenStore.readRefreshToken(token.split(" ")[1]);
//            System.out.println("before removing oAuth2RefToken = "+oAuth2RefToken);
//            if (oAuth2RefToken != null) {
//            	tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefToken);
//            	oAuth2RefToken = tokenStore.readRefreshToken(token.split(" ")[1]);
//                System.out.println("after removing oAuth2RefToken = "+oAuth2RefToken);
//            }
//        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
