package org.zalando.pazuzu.oauth2;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


public class ClientIdAuthorityGrantingAuthenticationExtractorTest {

    private static final String USER_ROLE = "ROLE_USER";

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private ClientIdAuthorityGrantingAuthenticationExtractor authenticationExtractor;

    @Before
    public void setUp() {

        authenticationExtractor =
                new ClientIdAuthorityGrantingAuthenticationExtractor(Sets.newSet("admin"), ADMIN_ROLE);
    }

    @Test
    public void shouldResolveOnlyUserRole() {

        // when
        OAuth2Authentication authentication = authenticationExtractor.extractAuthentication(
                scopes("tom"),
                "client-app"
        );

        // and
        List<String> authorities = authoritiesToRoleNames(authentication);

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities).containsOnly(USER_ROLE);
    }

    @Test
    public void shouldResolveUserRoleAndAdminRole() {

        // when
        OAuth2Authentication authentication = authenticationExtractor.extractAuthentication(
                scopes("admin"),
                "client-app"
        );

        // and
        List<String> authorities = authoritiesToRoleNames(authentication);

        // then
        assertThat(authorities).hasSize(2);
        assertThat(authorities).containsOnly(USER_ROLE, ADMIN_ROLE);
    }

    private static Map<String, Object> scopes(String principal) {
        return ImmutableMap.<String, Object>builder()
                .put("scope", Collections.singletonList("uid"))
                .put("uid", principal)
                .build();
    }

    private static List<String> authoritiesToRoleNames(OAuth2Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toList());
    }
}