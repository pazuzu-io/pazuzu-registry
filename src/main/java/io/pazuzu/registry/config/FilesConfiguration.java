package io.pazuzu.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FilesConfiguration {
    @Bean(name = "fileStorageRoot")
    public Path getFileStorageRoot() {
        return Paths.get("pazuzu", "files");
    }
}
