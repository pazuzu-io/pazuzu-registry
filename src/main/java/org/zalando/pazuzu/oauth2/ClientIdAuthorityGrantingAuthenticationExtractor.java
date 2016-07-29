package org.zalando.pazuzu.oauth2;

import com.google.common.collect.ImmutableSet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientIdAuthorityGrantingAuthenticationExtractor extends DefaultAuthenticationExtractor {

    private static final String USER_ROLE = "ROLE_USER";

    private final Set<String> userIds;

    private final Set<String> roles;

    public ClientIdAuthorityGrantingAuthenticationExtractor(Set<String> userIds, String... roles) {
        this.userIds = userIds;
        this.roles = ImmutableSet.copyOf(roles);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, Object> map, String clientId) {
        Object principal = getPrincipal(map);

        Set<String> roles = grantUserRoles(principal);

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                principal,
                "N/A",
                rolesToGrantedAuthorities(roles)
        );
        user.setDetails(map);

        OAuth2Request request = new OAuth2Request(null, clientId, null, true, resolveScopes(map), null, null, null, null);
        return new OAuth2Authentication(request, user);
    }

    private Set<String> grantUserRoles(Object principal) {
        ImmutableSet.Builder<String> userRoles = ImmutableSet.<String>builder().add(USER_ROLE);
        if (userIds.contains(principal)) {
            userRoles.addAll(roles);
        }
        return userRoles.build();
    }

    private Collection<? extends GrantedAuthority> rolesToGrantedAuthorities(Set<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
