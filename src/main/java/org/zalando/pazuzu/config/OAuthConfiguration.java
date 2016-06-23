package org.zalando.pazuzu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.pazuzu.oauth2.ClientIdAuthorityGrantingAuthenticationExtractor;
import org.zalando.pazuzu.properties.PazuzuRegistryProperties;
import org.zalando.pazuzu.security.Roles;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;
import org.zalando.stups.oauth2.spring.server.DefaultTokenInfoRequestExecutor;
import org.zalando.stups.oauth2.spring.server.ExecutorWrappers;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.stups.tokens.config.AccessTokensBeanProperties;

@Profile({"prod", "oauth"})
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableConfigurationProperties(PazuzuRegistryProperties.class)
public class OAuthConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private AccessTokensBeanProperties accessTokensBeanProperties;

    @Autowired
    private PazuzuRegistryProperties registryProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // here is the important part for stups-expression-handler
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        // @formatter:off
        http
            .httpBasic()
                .disable()
            .anonymous()
            .and()
                .requestMatchers()
                    .antMatchers("/api/**")
            .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
                .authorizeRequests()
                .antMatchers("/api/health").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers("/api/**").access("#oauth2.hasScope('uid')")
                .anyRequest().permitAll();
        // @formatter:on
    }

    @Bean
    public ResourceServerTokenServices customResourceTokenServices() {
        return new TokenInfoResourceServerTokenServices("pazuzu-registry",
                new ClientIdAuthorityGrantingAuthenticationExtractor(registryProperties.getAdmins(), Roles.ADMIN),
                ExecutorWrappers
                        .wrap(new DefaultTokenInfoRequestExecutor(
                                accessTokensBeanProperties.getTokenInfoUri().toString())));
    }
}
