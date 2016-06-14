package org.zalando.pazuzu;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zalando.pazuzu.mock.ResourceServerTokenServicesMock;
import org.zalando.stups.oauth2.spring.server.DefaultAuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { PazuzuAppLauncher.class, ResourceServerTokenServicesMock.class })
@WebIntegrationTest(randomPort = true)
public class ApiAuthTest extends AbstractComponentTest {
    @Autowired
    private TokenInfoResourceServerTokenServices resourceServerTokenServices;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    private static final String user = "user";
    private static final String admin = "test";
    private static final String VALID_TOKEN = "TOKEN_1234567890";

    private static final String body = "{\n" +
            "  \"name\": \"python\",\n" +
            "  \"docker_data\": \"RUN python\",\n" +
            "  \"test_instruction\": \"python -V\",\n" +
            "  \"dependencies\": []\n" +
            "}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .alwaysDo(print())
                .build();
    }

    private OAuth2Authentication buildTokenValidOauthResponse(final String uid) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("uid", uid);
        responseMap.put("scope", Stream.of("uid").collect(Collectors.toList()));
        responseMap.put("token_type", "Bearer");
        responseMap.put("expires_in", "4000");
        responseMap.put("access_token", VALID_TOKEN);

        return new DefaultAuthenticationExtractor().extractAuthentication(responseMap, "what_up");
    }

    @After
    public void clearTokenServiceMocks() {
        //  Clear out any token service mocking between the test methods to allow us to do both
        //  positive and negative auth testing in the same context.
        Mockito.reset(this.resourceServerTokenServices);
    }

    /**
     * Test post feature without oauth.
     * @throws Exception
     */
    @Test
    public void postFeaturesNoOAuth2() throws Exception {
        mockMvc.perform(post("/api/features")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    /**
     * Test put feature without oauth.
     * @throws Exception
     */
    @Test
    public void putFeaturesNoOAuth2() throws Exception {
        mockMvc.perform(put("/api/features")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    /**
     * Test delete feature without oauth.
     * @throws Exception
     */
    @Test
    public void deleteFeaturesNoOAuth2() throws Exception {
        mockMvc.perform(delete("/api/features")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }


    /**
     * Test post feature with invalid token.
     * @throws Exception
     */
    @Test
    public void postFeaturesInvalidToken() throws Exception {
        mockMvc.perform(post("/api/features")
                .header("Authorization", "Bearer " + "fake")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_token")));
    }

    /**
     * Test put feature with invalid token.
     * @throws Exception
     */
    @Test
    public void putFeaturesInvalidToken() throws Exception {
        mockMvc.perform(put("/api/features")
                .header("Authorization", "Bearer " + "fake")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_token")));
    }

    /**
     * Test delete feature with invalid token.
     * @throws Exception
     */
    @Test
    public void deleteFeaturesInvalidToken() throws Exception {
        mockMvc.perform(delete("/api/features")
                .header("Authorization", "Bearer " + "fake")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("invalid_token")));
    }

    /**
     * Test getting features without auth.
     * @throws Exception
     */
    @Test
    public void getFeaturesSuccess() throws Exception {
        mockMvc.perform(get("/api/features")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test post feature as normal user.
     */
    @Test
    public void postFeaturesAsUser() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(user));

        mockMvc.perform(post("/api/features")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * Test put feature as normal user.
     */
    @Test
    public void putFeaturesAsUser() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(user));

        mockMvc.perform(put("/api/features/python")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Test delete feature as normal user.
     */
    @Test
    public void deleteFeaturesAsUser() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(user));

        mockMvc.perform(delete("/api/features/python")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    /**
     * Test post feature as admin.
     */
    @Test
    public void postFeaturesAsAdmin() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(admin));

        mockMvc.perform(post("/api/features")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Test put feature as admin.
     */
    @Test
    public void putFeaturesAsAdmin() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(admin));

        mockMvc.perform(post("/api/features")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/features/python")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test delete feature as admin.
     */
    @Test
    public void deleteFeaturesAsAdmin() throws Exception {
        when(this.resourceServerTokenServices.loadAuthentication(anyString())).thenReturn(buildTokenValidOauthResponse(admin));

        mockMvc.perform(post("/api/features")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/features/python")
                .header("Authorization", "Bearer " + VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
