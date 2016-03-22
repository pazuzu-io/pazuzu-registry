package org.zalando.pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureToAddDto {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }
}
