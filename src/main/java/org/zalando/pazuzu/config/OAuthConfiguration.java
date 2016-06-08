package org.zalando.pazuzu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
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

import java.util.Iterator;
import java.util.List;

@Configuration
@EnableResourceServer
@Profile(value = {"production", "test_oauth"})
public class OAuthConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private AccessTokensBeanProperties accessTokensBeanProperties;

    private static final String scopeMatcher = "#oauth2.hasScope('uid')";
    private String adminMatcher;

    @Value("#{'${pazuzu-registry.admins}'.split(',')}")
    private List<String> adminList;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // here is the important part for stups-expression-handler
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    private String buildAdminUserMatcher() {
        Iterator<String> iterator = adminList.iterator();
        StringBuilder result = new StringBuilder();

        result.append(scopeMatcher);

        if (adminList != null && !adminList.isEmpty()) {
            result.append(" and (");

            while (iterator.hasNext()) {
                result.append("authentication.name.equals('");
                result.append(iterator.next());
                result.append("')");

                if (iterator.hasNext()) {
                    result.append(" or ");
                }
            }
            result.append(")");
        }

        return result.toString();
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        if (adminMatcher == null || adminMatcher.isEmpty()) {
            adminMatcher = buildAdminUserMatcher();
        }

        // @formatter:off
        http
            .httpBasic().disable()
                .requestMatchers().antMatchers("/**")
        .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
            .authorizeRequests()
                .antMatchers("/api/health").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").access(scopeMatcher)
                .antMatchers(HttpMethod.PUT, "/api/**").access(adminMatcher)
                .antMatchers(HttpMethod.DELETE, "/api/**").access(adminMatcher)
                .antMatchers(HttpMethod.GET, "/api/**").permitAll();
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
