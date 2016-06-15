package org.zalando.pazuzu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.social.zauth.api.ZAuth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Configuration
@Profile("test")
public class FixturesConfiguration {

    @Bean
    public ZAuth zAuth() {
        ZAuth mock = mock(ZAuth.class);

        when(mock.getCurrentLogin()).thenReturn("@test_user");
        return mock;
    }
}
