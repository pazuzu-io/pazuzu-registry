package org.zalando.pazuzu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class MapperConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ddZ", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return new ObjectMapper()
                .setDateFormat(dateFormat)
                .registerModule(new Jdk8Module())
                .registerModule(new ProblemModule());
    }

}
