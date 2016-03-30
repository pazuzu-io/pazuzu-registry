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
        assertThat(response.getBody().split("\n")).contains(
                "FROM ubuntu:latest",
                "sudo apt-get feature1",
                "sudo apt-get feature3"
        );
    }

    @Test
    public void dockerfileWithFeaturesWithDependenciesIsCreatedCorrectly() throws Exception {
        // given
        createFeature("1st_level_feature_1", "sudo apt-get 1st_level_feature_1");
        createFeature("1st_level_feature_2", "sudo apt-get 1st_level_feature_2");
        createFeature("2nd_level_feature_1", "sudo apt-get 2nd_level_feature_1", "1st_level_feature_1");
        createFeature("2nd_level_feature_2", "sudo apt-get 2nd_level_feature_2", "1st_level_feature_2");
        createFeature("3rd_level_feature_1", "sudo apt-get 3rd_level_feature_1", "2nd_level_feature_1");
        createFeature("3rd_level_feature_2", "sudo apt-get 3rd_level_feature_2", "1st_level_feature_2", "2nd_level_feature_2");

        // when
        ResponseEntity<String> response = template.getForEntity(url("/api/dockerfile") + "?features=3rd_level_feature_2&features=2nd_level_feature_1", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody().split("\n")).containsSubsequence(
                "FROM ubuntu:latest",
                "sudo apt-get 1st_level_feature_1"
        );

        assertThat(response.getBody().split("\n")).containsSubsequence(
                "FROM ubuntu:latest",
                "sudo apt-get 1st_level_feature_2"
        );

        assertThat(response.getBody().split("\n")).containsSubsequence(
                "FROM ubuntu:latest",
                "sudo apt-get 1st_level_feature_2",
                "sudo apt-get 2nd_level_feature_2"
        );

        assertThat(response.getBody().split("\n")).containsSubsequence(
                "FROM ubuntu:latest",
                "sudo apt-get 1st_level_feature_2",
                "sudo apt-get 2nd_level_feature_2",
                "sudo apt-get 3rd_level_feature_2"
        );
    }

    @Test
    public void errorIsReturnedWhenCreatingDockerfileWithNotExistingFeatures() throws Exception {
        // when
        ResponseEntity<String> response = template.getForEntity(url("/api/dockerfile") + "?features=non_existing_feature", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map json = mapper.readValue(response.getBody(), Map.class);
        assertThat(json.get("code")).isEqualTo("feature_not_found");
        assertThat(json.get("message")).isNotNull();
    }
}
