package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.pazuzu.feature.tag.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FeatureDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("docker_data")
    private String dockerData;
    @JsonProperty("test_instruction")
    private String testInstruction;
    @JsonProperty("description")
    private String description;

    public List<Tag> getTags() {
        return (null != tags) ? tags : Collections.emptyList();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty("tags")
    private List<Tag> tags;

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

    public static FeatureDto populate(String name, String dockerData, String testInstruction, String description , List<Tag> tags) {
        final FeatureDto result = new FeatureDto();
        result.name = name;
        result.dockerData = dockerData;
        result.testInstruction = testInstruction;
        result.description = description;
        result.tags = tags;
        return result;
    }

    public static FeatureDto populate(String name, String dockerData, String testInstruction, String description) {
        return populate(name,dockerData,testInstruction,description, Collections.emptyList());
    }

    protected static void fillShort(Feature feature, FeatureDto result) {
        result.name = feature.getName();
        result.dockerData = feature.getDockerData();
        result.testInstruction = feature.getTestInstruction();
        result.description = feature.getDescription();
        result.tags = feature.getTags();
    }

    public String getTestInstruction() {
        return testInstruction;
    }

    public void setTestInstruction(String testInstruction) {
        this.testInstruction = testInstruction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureDto that = (FeatureDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(dockerData, that.dockerData) &&
                Objects.equals(testInstruction, that.testInstruction) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dockerData, testInstruction);
    }
}
