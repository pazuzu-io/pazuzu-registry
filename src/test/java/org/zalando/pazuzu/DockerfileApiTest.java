package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DockerfileApiTest extends AbstractComponentTest {

    @Test
    public void dockerfileIsCreatedCorrectly() throws Exception {

        // given
        createFeature("feature1", "sudo apt-get feature1");
        createFeature("feature2", "sudo apt-get feature2");
        createFeature("feature3", "sudo apt-get feature3");

        // when
        ResponseEntity<String> response = template.getForEntity(url("/api/dockerfile") + "?features=feature1&features=feature3", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("FROM ubuntu:latest");
        assertThat(response.getBody()).contains("sudo apt-get feature1");
        assertThat(response.getBody()).contains("sudo apt-get feature3");
    }

    @Test
    public void errorIsReturnedWhenCreatingDockerfileWithNotExistingFeatures() throws Exception {
        // when
        ResponseEntity<String> response = template.getForEntity(url("/api/dockerfile") + "?features=non_existing_feature", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map json = mapper.readValue(response.getBody(), Map.class);
        assertThat(json.get("code")).isEqualTo("features_not_present");
        assertThat(json.get("message")).isNotNull();
    }

}
