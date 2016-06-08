package org.zalando.pazuzu.mock;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

@Configuration
public class ResourceServerTokenServicesMock {
    @Bean
    public ResourceServerTokenServices customResourceTokenServices() {
        return Mockito.mock(TokenInfoResourceServerTokenServices.class);
    }
}
