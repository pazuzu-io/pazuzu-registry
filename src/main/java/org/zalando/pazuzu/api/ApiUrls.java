package org.zalando.pazuzu.api;

public final class ApiUrls {

    /*
     * Ant matcher used for auth config.
     */
    public static final String BASE_API_ANT_MATCHER = "/api/**";

    // Health endpoint
    public static final String HEALTH =  "/api/health";

    // Features endpoints.
    public static final String FEATURES = "/api/features";
    public static final String FEATURE_NAME = "/api/features/{featureName}";

    private ApiUrls() {}
}
