/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pazuzu.config;

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

/**
 *
 * @author jbellmann
 */
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
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
                .authorizeRequests()
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
