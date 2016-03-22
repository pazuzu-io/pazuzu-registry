package pazuzu.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import pazuzu.ServiceExceptionMapper;
import pazuzu.UnknownExceptionMapper;

@Component
@SuppressWarnings("unused")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("pazuzu.web");
        registerClasses(UnknownExceptionMapper.class);
        registerClasses(ServiceExceptionMapper.class);
    }
}