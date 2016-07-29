package org.zalando.pazuzu.feature.file;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.AbstractComponentTest;
import org.zalando.pazuzu.feature.FeatureDto;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.net.URI;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pzalunin on 26/07/16.
 */
public class FileResourceTest extends AbstractComponentTest {

    @Autowired
    private InMemoryFileContentService contentService;

    @Before
    public void cleanupContent() {
        contentService.clean();
    }

    @Test
    public void testCRD() throws Exception {
        final String featureName = "feature-1";
        final ByteArrayResource configYml = new ByteArrayResource("-- config".getBytes());
        final ByteArrayResource userJar = new ByteArrayResource("JAR".getBytes());

        final FeatureDto feature = new FeatureDto();
        feature.setDescription("Feature-1");
        feature.setName("feature-1");
        feature.setDockerData("COPY config.yml /\n" +
                "COPY jars/user.jar /");

        final ResponseEntity<FeatureFullDto> resp = template.postForEntity(url("/api/features"), feature, FeatureFullDto.class);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());

        final ResponseEntity<FileDto> configYmlResp = template.postForEntity(url("/api/features/{featureName}/files?name={fileName}"),
                configYml, FileDto.class, featureName, "config.yml");

        assertEquals(HttpStatus.CREATED, configYmlResp.getStatusCode());
        assertEquals("config.yml", configYmlResp.getBody().getName());

        final ResponseEntity<FileDto> userJarResp = template.postForEntity(url("/api/features/{featureName}/files?name={fileName}"),
                userJar, FileDto.class, featureName, "jars/user.jar");

        assertEquals(HttpStatus.CREATED, userJarResp.getStatusCode());
        assertEquals("jars/user.jar", userJarResp.getBody().getName());

        ResponseEntity<FileDto[]> filesResp = template.getForEntity(url("/api/features/{featureName}/files"),
                FileDto[].class, featureName);

        assertEquals(HttpStatus.OK, filesResp.getStatusCode());
        assertEquals(2, filesResp.getBody().length);

        assertTrue(stream(filesResp.getBody()).anyMatch(f -> f.getName().equals("jars/user.jar")));
        assertTrue(stream(filesResp.getBody()).anyMatch(f -> f.getName().equals("config.yml")));

        final ResponseEntity<FeatureFullDto> featureResp = template.getForEntity(url("/api/features/{featureName}"), FeatureFullDto.class,
                featureName);

        assertEquals(HttpStatus.OK, featureResp.getStatusCode());
        assertEquals(2, featureResp.getBody().getFiles().size());

        assertTrue(featureResp.getBody().getFiles().stream().anyMatch(f -> f.getName().equals("config.yml")));
        assertTrue(featureResp.getBody().getFiles().stream().anyMatch(f -> f.getName().equals("jars/user.jar")));

        final ResponseEntity<ByteArrayResource> configYmlCntntResp = template.getForEntity(url(configYmlResp.getBody().getContentHref()),
                ByteArrayResource.class);

        assertEquals(HttpStatus.OK, configYmlCntntResp.getStatusCode());
        assertEquals(configYml, configYmlCntntResp.getBody());

        final ResponseEntity<ByteArrayResource> userJarCntntResp = template.getForEntity(url(userJarResp.getBody().getContentHref()),
                ByteArrayResource.class);

        assertEquals(HttpStatus.OK, userJarCntntResp.getStatusCode());
        assertEquals(userJar, userJarCntntResp.getBody());

        final ResponseEntity<Object> configYmlDelResp = template.exchange(
                RequestEntity.delete(new URI(url("/api/features/" + featureName + "/files/" + configYmlResp.getBody().getId()))).build(),
                Object.class
        );

        assertEquals(HttpStatus.OK, configYmlDelResp.getStatusCode());

        filesResp = template.getForEntity(url("/api/features/{featureName}/files"),
                FileDto[].class, featureName);

        assertEquals(HttpStatus.OK, filesResp.getStatusCode());
        assertEquals(1, filesResp.getBody().length);

        assertTrue(stream(filesResp.getBody()).anyMatch(f -> f.getName().equals("jars/user.jar")));
    }
}