package com.royasoftware.settings.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
public class OAuth2ServerConfiguration {

    private static final String RESOURCE_ID = "restservice";

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        
        @Autowired
        private TokenStore tokenStore;

        private CustomLogoutSuccessHandler customLogoutSuccessHandler;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
//        	TokenStore ts = new JwtTokenStore(jwtAccessTokenConverter);
        	customLogoutSuccessHandler = new CustomLogoutSuccessHandler(tokenStore);
            // @formatter:off
            resources.resourceId(RESOURCE_ID).tokenStore(tokenStore);
            // @formatter:on
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http	
                    .csrf().disable()
//                    .anonymous().and()
                    .authorizeRequests()
//                    .antMatchers("/api/todo/img/**").permitAll()
//                    .antMatchers("/api/**").authenticated()//.hasAnyRole("ROLE_USER")//
            		.antMatchers("/**").authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error=1")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutUrl("/oauth/logout")
                    .logoutSuccessUrl("/")
                    .logoutSuccessHandler(customLogoutSuccessHandler)
                    .permitAll()
                    ;
//            		.antMatchers("/**").authenticated();
            // @formatter:on
//            http.addFilterBefore( new Auth2FilterRequest(),null);
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private JwtAccessTokenConverter jwtAccessTokenConverter;

//        private TokenStore tokenStore = new InMemoryTokenStore();
        @Autowired
        private TokenStore tokenStore;
//        private TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter);

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;
        
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // @formatter:off
        	
            endpoints.tokenStore(tokenStore)
                    .authenticationManager(authenticationManager)
                    .accessTokenConverter(jwtAccessTokenConverter)
                    .tokenEnhancer(new CustomTokenEnhancer());
            // @formatter:on
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // @formatter:off
            clients .inMemory()
                    .withClient("clientapp")
                    .authorizedGrantTypes("password","refresh_token")
                    .authorities("USER")
                    .scopes("read", "write")
                    .resourceIds(RESOURCE_ID)
                    .secret("123456");
            // @formatter:on
        }

    }
    @Bean
    public TokenStore tokenStore() {
//    	TokenStore ts = new JwtTokenStore(jwtAccessTokenConverter);
    	TokenStore ts = new InMemoryTokenStore();
        return ts;
    }
    
    
//    @Bean
//    @Primary
//    public AuthorizationServerTokenServices tokenServices() {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        // ...
//        tokenServices.setTokenEnhancer(tokenEnhancer());
//        return tokenServices;
//    }
    
//    @Bean
//    public TokenEnhancer tokenEnhancer() {
//        return new CustomTokenEnhancer();
//    }

}
