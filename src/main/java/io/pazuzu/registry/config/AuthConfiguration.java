package io.pazuzu.registry.config;

import io.pazuzu.registry.properties.PazuzuRegistryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableConfigurationProperties(PazuzuRegistryProperties.class)
public class AuthConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private PazuzuRegistryProperties registryProperties;

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
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/health").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers("/api/**").permitAll()
                //FIXME: disabled oauth
                .anyRequest().permitAll();
        // @formatter:on
    }

}
