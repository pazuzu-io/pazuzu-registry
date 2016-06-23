package org.zalando.pazuzu;


import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.zalando.pazuzu.feature.FeatureDto;
import org.zalando.pazuzu.feature.FeatureToCreateDto;
import org.zalando.pazuzu.oauth2.ClientIdAuthorityGrantingAuthenticationExtractor;
import org.zalando.pazuzu.security.Roles;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.zalando.pazuzu.assertion.RestTemplateAssert.*;

@ActiveProfiles("oauth")
public class ApiAuthenticationTest extends AbstractComponentTest {

    private static final String ADMIN_TOKEN = "admin";

    private static final String ADMIN_UID = "admin";

    private static final String USER_TOKEN = "user";

    private static final String USER_UID = "user";

    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    @Before
    public void setUp() {
        when(resourceServerTokenServices.loadAuthentication(USER_TOKEN))
                .thenReturn(authentication(USER_UID, USER_TOKEN));
        when(resourceServerTokenServices.loadAuthentication(ADMIN_TOKEN))
                .thenReturn(authentication(ADMIN_UID, ADMIN_TOKEN));
    }

    @Test
    public void shouldFailToCreateFeatureWithoutAuthorization() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // when
        ResponseEntity response = template.exchange(
                url("/api/features"), POST, new HttpEntity<>(feature), Object.class
        );

        // then
        assertUnauthorized(response);
    }

    @Test
    public void shouldCreateFeatureWithUserAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // when
        ResponseEntity response = template.exchange(
                url("/api/features"), POST, new HttpEntity<>(feature, oauthToken(USER_TOKEN)), Object.class
        );

        // then
        assertCreated(response);
    }

    @Test
    public void shouldFailToUpdateFeatureWithoutAuthorization() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), PUT, new HttpEntity<>(feature), Object.class
        );

        // then
        assertUnauthorized(response);
    }

    @Test
    public void shouldFailToUpdateFeatureWithUserAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), PUT, new HttpEntity<>(feature, oauthToken(USER_TOKEN)), Object.class
        );

        // then
        assertForbidden(response);
    }

    @Test
    public void shouldUpdateFeatureWithAdminAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), PUT, new HttpEntity<>(feature, oauthToken(ADMIN_TOKEN)), Object.class
        );

        // then
        assertSuccess(response);
    }

    @Test
    public void shouldFailToDeleteFeatureWithoutAuthorization() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), DELETE, null, Object.class
        );

        // then
        assertUnauthorized(response);
    }

    @Test
    public void shouldFailToDeleteFeatureWithUserAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), DELETE, new HttpEntity<>(oauthToken(USER_TOKEN)), Object.class
        );

        // then
        assertForbidden(response);
    }

    @Test
    public void shouldDeleteFeatureWithAdminAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName()), DELETE, new HttpEntity<>(oauthToken(ADMIN_TOKEN)), Object.class
        );

        // then
        assertNoContent(response);
    }

    @Test
    public void shouldFailToApproveFeatureWithUserAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName(), "approve"), PUT, new HttpEntity<>(oauthToken(USER_TOKEN)), Object.class
        );

        // then
        assertForbidden(response);
    }

    @Test
    public void shouldApproveFeatureWithAdminAuthority() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity response = template.exchange(
                url("/api/features", feature.getName(), "approve"), PUT, new HttpEntity<>(oauthToken(ADMIN_TOKEN)), Object.class
        );

        // then
        assertNoContent(response);
    }

    @Test
    public void shouldRetrieveFeatureListAnonymously() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity<List<FeatureDto>> response = template.exchange(
                url("/api/features"), GET, null, new ParameterizedTypeReference<List<FeatureDto>>() {}
        );

        // then
        assertSuccess(response);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void shouldRetrieveFeatureListByNameAnonymously() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity<List<FeatureDto>> response = template.exchange(
                url("/api/features/search", feature.getName()), GET, null, new ParameterizedTypeReference<List<FeatureDto>>() {}
        );

        // then
        assertSuccess(response);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void shouldRetrieveFeatureByNameAnonymously() {

        // given
        FeatureToCreateDto feature = createTestFeature();

        // and
        createFeature(feature);

        // when
        ResponseEntity<FeatureDto> response = template.exchange(
                url("/api/features/", feature.getName()), GET, null, FeatureDto.class
        );

        // then
        assertSuccess(response);
        assertThat(response.getBody()).isNotNull();
    }

    private void createFeature(FeatureToCreateDto feature) {
        template.exchange(
                url("/api/features"), POST, new HttpEntity<Object>(feature, oauthToken(ADMIN_TOKEN)), Object.class
        );
    }

    private MultiValueMap<String, String> oauthToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("Bearer %s", token));
        return headers;
    }

    private FeatureToCreateDto createTestFeature() {
        FeatureToCreateDto feature = new FeatureToCreateDto();
        feature.setName("test_feature");
        return feature;
    }

    private static OAuth2Authentication authentication(String uid, String token) {
        Map<String, Object> responseMap = ImmutableMap.<String, Object>builder()
                .put("uid", uid)
                .put("scope", Collections.singleton("uid"))
                .put("token_type", "Bearer")
                .put("expires_in", "4000")
                .put("access_token", token)
                .build();

        return new ClientIdAuthorityGrantingAuthenticationExtractor(Collections.singleton("admin"), Roles.ADMIN)
                .extractAuthentication(responseMap, "clientid");
    }

    @Configuration
    public static class TestConfiguration {

        @Bean
        @Primary
        public ResourceServerTokenServices mockResourceServerTokenService() {

            return mock(TokenInfoResourceServerTokenServices.class);
        }
    }
}
