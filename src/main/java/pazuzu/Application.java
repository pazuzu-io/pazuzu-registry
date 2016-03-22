package pazuzu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan(basePackages = "pazuzu.model")
@ComponentScan(basePackages = {"pazuzu.service", "pazuzu.dao", "pazuzu.config"})
@EnableAutoConfiguration
public class Application {
    public static void main(final String[] args) {
        new SpringApplication(Application.class).run(args);
    }
}
