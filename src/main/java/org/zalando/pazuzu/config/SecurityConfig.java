package org.zalando.pazuzu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.zalando.pazuzu.oauth2.SimpleSocialUserDetailsService;

import java.util.ArrayList;

@Configuration
@EnableWebSecurity
@Profile(value = "production")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        //J-
        // @formatter:off
        http
            .formLogin()
                .loginPage("/signin")
                .failureUrl("/signin?error=bad_credentials")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
        .and()
            .authorizeRequests()
                .antMatchers("/favicon.ico", "/static/**", "/signup")
                    .permitAll()
                .antMatchers("/**")
                    .authenticated()
        .and()
            .rememberMe()
        .and()
            .apply((SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>)
                    new SpringSocialConfigurer());
        // @formatter:on
        //J+

    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsManager());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsManager();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        // TODO: replace with a persistent database when we have one
        return new InMemoryUserDetailsManager(new ArrayList<>());
    }

}
