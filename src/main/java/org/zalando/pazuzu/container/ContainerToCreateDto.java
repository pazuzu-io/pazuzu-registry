package org.zalando.pazuzu.container;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class ContainerToCreateDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("features")
    private List<String> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = new LinkedList<>(features);
    }
}
