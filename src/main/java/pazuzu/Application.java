package pazuzu;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@EntityScan(basePackages = "pazuzu.model")
@ComponentScan(basePackages = "pazuzu.service")
@EnableAutoConfiguration
public class Application {
    @Component
    @SuppressWarnings("unused")
    public static class JerseyConfig extends ResourceConfig {
        public JerseyConfig() {
            packages("pazuzu.web");
            registerClasses(UnknownExceptionMapper.class);
        }
    }

    public static void main(final String[] args) {
        final SpringApplication application = new SpringApplication(Application.class);
        application.setShowBanner(false);
        application.run(args);
    }

}
