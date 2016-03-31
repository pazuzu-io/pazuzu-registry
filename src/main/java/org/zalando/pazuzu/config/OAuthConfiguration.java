package org.zalando.pazuzu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.DefaultTokenInfoRequestExecutor;
import org.zalando.stups.oauth2.spring.server.ExecutorWrappers;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.stups.tokens.config.AccessTokensBeanProperties;

@Configuration
@EnableResourceServer
@Profile(value = "production")
public class OAuthConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private AccessTokensBeanProperties accessTokensBeanProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // here is the important part for stups-expression-handler
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        // @formatter:off
        http
            .httpBasic().disable()
            .requestMatchers().antMatchers("/api/**")
        .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
            .authorizeRequests()
                .antMatchers("/api/health").permitAll()
                .antMatchers("/api/**").access("#oauth2.hasScope('uid')");
        // @formatter:on
    }

    @Bean
    public ResourceServerTokenServices customResourceTokenServices() {
        return new TokenInfoResourceServerTokenServices("jaxrs",
                new DefaultAuthenticationExtractor(),
                ExecutorWrappers
                        .wrap(new DefaultTokenInfoRequestExecutor(
                                accessTokensBeanProperties.getTokenInfoUri().toString())));
    }

}
