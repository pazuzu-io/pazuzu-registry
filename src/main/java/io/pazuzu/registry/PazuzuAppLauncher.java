package io.pazuzu.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.zalando.twintip.spring.SchemaResource;

//TODO Optimise spring boot bean/component/config loading.
@SpringBootApplication
@Import(SchemaResource.class)
public class PazuzuAppLauncher {

    public static void main(final String[] args) {
        SpringApplication.run(PazuzuAppLauncher.class, args);
    }
}
