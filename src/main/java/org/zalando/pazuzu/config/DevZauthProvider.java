package org.zalando.pazuzu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.social.zauth.api.ZAuth;
import org.zalando.zauth.teams.TeamsOperations;
import org.zalando.zauth.users.UsersOperations;

@Configuration
@Profile("dev")
public class DevZauthProvider {

    @Bean
    public ZAuth zAuth() {
        return new ZAuth() {
            @Override
            public String getCurrentLogin() {
                return "devuser";
            }

            @Override
            public UsersOperations userOperations() {
                return null;
            }

            @Override
            public TeamsOperations teamsOperations() {
                return null;
            }
        };
    }
}

