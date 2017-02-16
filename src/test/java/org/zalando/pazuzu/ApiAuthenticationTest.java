package org.zalando.pazuzu;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.zalando.pazuzu.assertion.RestTemplateAssert.assertCreated;
import static org.zalando.pazuzu.assertion.RestTemplateAssert.assertSuccess;
import static org.zalando.pazuzu.assertion.RestTemplateAssert.assertUnauthorized;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.model.Review;
import org.zalando.pazuzu.oauth2.ClientIdAuthorityGrantingAuthenticationExtractor;
import org.zalando.pazuzu.security.Roles;

import com.google.common.collect.ImmutableMap;

@ActiveProfiles({"oauth", "test"})
public class ApiAuthenticationTest extends AbstractComponentTest {

    private static final String ADMIN_TOKEN = "admin";

    private static final String ADMIN_UID = "admin";

    private static final String USER_TOKEN = "user";

    private static final String USER_UID = "user";

    @MockBean
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
        Feature feature = createTestFeature();

        // when
        ResponseEntity<?> response = template.exchange(
                url(featuresUrl), POST, new HttpEntity<>(feature), Object.class
        );

        // then
        assertUnauthorized(response);
    }

    @Test
    public void shouldCreateFeatureWithUserAuthority() {

        // given
        Feature feature = createTestFeature();

        // when
        ResponseEntity<?> response = template.exchange(
                url(featuresUrl), POST, new HttpEntity<>(feature, oauthToken(USER_TOKEN)), Object.class
        );

        // then
        assertCreated(response);
    }

    @Test
    public void shouldSetAuthorForCreatedFeatureWithUserAuthority() {

        // given
        Feature feature = createTestFeature();

        // when
        ResponseEntity<Feature> response = template.exchange(
                url(featuresUrl), POST, new HttpEntity<>(feature, oauthToken(USER_TOKEN)), Feature.class
        );

        // then
        assertCreated(response);
        assertThat(response.getBody().getMeta().getAuthor()).isEqualTo(USER_UID);
    }

    @Test
    public void shouldRetrieveFeatureListAnonymously() {

        // given
        Feature feature = createTestFeature();

        // and
        createFeatureOAuth(feature);

        // and (anonymous user can only list approved features)
        Review review = new Review();
        review.setReviewStatus(Review.ReviewStatusEnum.approved);
        ResponseEntity<Object> approval = template.exchange(
                url(featuresUrl, feature.getMeta().getName(), reviewPath), POST, new HttpEntity<>(review, oauthToken(ADMIN_TOKEN)), Object.class
        );
        assertCreated(approval);

        // when
        ResponseEntity<FeatureList> response = template.exchange(
                url(featuresUrl), GET, null, FeatureList.class
        );

        // then
        assertSuccess(response);
        assertThat(response.getBody().getFeatures()).hasSize(1);
    }

    @Test
    public void shouldRetrieveFeatureByNameAnonymously() {

        // given
        Feature feature = createTestFeature();

        // and
        createFeatureOAuth(feature);

        // when
        ResponseEntity<Feature> response = template.exchange(
                url(featuresUrl, feature.getMeta().getName()), GET, null, Feature.class
        );

        // then
        assertSuccess(response);
        assertThat(response.getBody()).isNotNull();
    }

    private void createFeatureOAuth(Feature feature) {
        template.exchange(
                url(featuresUrl), POST, new HttpEntity<Object>(feature, oauthToken(ADMIN_TOKEN)), Object.class
        );
    }

    private MultiValueMap<String, String> oauthToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", String.format("Bearer %s", token));
        return headers;
    }

    private Feature createTestFeature() {
        Feature feature = new Feature();
        feature.setMeta(new FeatureMeta());
        feature.getMeta().setName("test_feature");
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

}
