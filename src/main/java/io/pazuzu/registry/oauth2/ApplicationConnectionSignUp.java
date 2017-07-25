package io.pazuzu.registry.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import java.util.Collections;

public class ApplicationConnectionSignUp implements ConnectionSignUp {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConnectionSignUp.class);

    private final UserDetailsManager userDetailsManager;

    public ApplicationConnectionSignUp(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        String username = profile.getUsername();

        logger.info("Creating user with id: {}", username);
        User user = new User(username, "", Collections.emptyList());

        userDetailsManager.createUser(user);
        return username;
    }
}
