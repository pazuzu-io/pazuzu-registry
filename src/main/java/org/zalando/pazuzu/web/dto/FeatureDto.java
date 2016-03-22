package org.zalando.pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.pazuzu.model.Feature;

public class FeatureDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("docker_data")
    private String dockerData;

    public String getName() {
        return name;
    }

    public String getDockerData() {
        return dockerData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDockerData(String dockerData) {
        this.dockerData = dockerData;
    }

    public static FeatureDto ofShort(Feature feature) {
        if (null == feature) {
            return null;
        }
        final FeatureDto result = new FeatureDto();
        fillShort(feature, result);
        return result;
    }

    protected static void fillShort(Feature feature, FeatureDto result) {
        result.name = feature.getName();
        result.dockerData = feature.getDockerData();
    }

}
