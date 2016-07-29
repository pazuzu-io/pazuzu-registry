package org.zalando.pazuzu.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties("pazuzu.registry")
public class PazuzuRegistryProperties {

    private Set<String> admins = new HashSet<>();

    public Set<String> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<String> admins) {
        this.admins = admins;
    }
}
