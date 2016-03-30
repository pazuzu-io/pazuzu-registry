package org.zalando.pazuzu.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.oauth2.ClientCredentialsSupplier;
import org.springframework.social.oauth2.FileCredentialsSupplierSupport;
import org.springframework.social.zauth.api.ZAuth;
import org.springframework.social.zauth.config.AbstractZAuthSocialConfigurer;
import org.zalando.pazuzu.oauth2.ApplicationConnectionSignUp;
import org.zalando.stups.tokens.config.AccessTokensBeanProperties;

import java.io.IOException;

@Configuration
@EnableSocial
@Profile(value = "production")
public class SocialConfig extends AbstractZAuthSocialConfigurer {

    @Autowired
    private AccessTokensBeanProperties accessTokensBeanProperties;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Override
    protected UsersConnectionRepository doGetUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        // TODO: replace with a persistent database when we have one
        InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        repository.setConnectionSignUp(new ApplicationConnectionSignUp(userDetailsManager));
        return repository;
    }

    @Override
    protected ClientCredentialsSupplier getClientCredentialsSupplier() {
        return new CredentialFileSupplier(accessTokensBeanProperties.getCredentialsDirectory());
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ZAuth zAuth(final ConnectionRepository repository) {
        Connection<ZAuth> conn = repository.findPrimaryConnection(ZAuth.class);
        return conn == null ? null : conn.getApi();
    }

}

class ClientDto {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}

class CredentialFileSupplier extends FileCredentialsSupplierSupport implements ClientCredentialsSupplier {

    public CredentialFileSupplier(String credentialsDirectoryPath) {
        super(credentialsDirectoryPath);
    }

    public ClientDto getClientCredentials() {
        try {
            return readAsJson("client.json", ClientDto.class);
        } catch (IOException e) {
            throw new IllegalStateException("Client credentials not found on "
                    + getFile("client.json").getAbsolutePath());
        }
    }

    @Override
    public String getClientId() {
        return getClientCredentials().getClientId();
    }

    @Override
    public String getClientSecret() {
        return getClientCredentials().getClientSecret();
    }
}