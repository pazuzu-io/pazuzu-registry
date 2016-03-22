package pazuzu;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@EntityScan(basePackages = "pazuzu.model")
@ComponentScan(basePackages = {"pazuzu.service", "pazuzu.dao"})
@EnableAutoConfiguration
public class Application {

    @Component
    @SuppressWarnings("unused")
    public static class JerseyConfig extends ResourceConfig {
        public JerseyConfig() {
            packages("pazuzu.web");
            registerClasses(UnknownExceptionMapper.class);
            registerClasses(ServiceExceptionMapper.class);
            registerClasses(AnyExceptionMapper.class);
        }
    }

    public static void main(final String[] args) {
        new SpringApplication(Application.class).run(args);
    }
}
