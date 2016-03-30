package org.zalando.pazuzu.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import java.util.Collections;

/**
 * Created by mdasilvatrov on 29/03/16.
 */
public class SimpleSocialUserDetailsService implements SocialUserDetailsService{

    private final UserDetailsManager userDetailsManager;

    public SimpleSocialUserDetailsService(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String username) throws UsernameNotFoundException {
        UserDetails details = userDetailsManager.loadUserByUsername(username);
        return new SocialUser(details.getUsername(), "", AuthorityUtils.createAuthorityList("USER"));
    }
}
