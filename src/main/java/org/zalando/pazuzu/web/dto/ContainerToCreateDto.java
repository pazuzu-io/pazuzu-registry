package org.zalando.pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ContainerToCreateDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("features")
    private List<String> features;

    public String getName() {
        return name;
    }

    public List<String> getFeatures() {
        return features;
    }
}
