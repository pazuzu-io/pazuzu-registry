package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.pazuzu.feature.file.FileDto;
import org.zalando.pazuzu.feature.tag.TagDto;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("docker_data")
    private String dockerData;
    @JsonProperty("test_instruction")
    private String testInstruction;
    @JsonProperty("description")
    private String description;
    @JsonProperty("tags")
    private List<TagDto> tags;
    @JsonProperty("approved")
    private boolean approved;
    @JsonProperty("files")
    private Set<FileDto> files = new HashSet<>();

    public static FeatureDto ofShort(Feature feature) {
        if (null == feature) {
            return null;
        }
        final FeatureDto result = new FeatureDto();
        fillShort(feature, result);
        return result;
    }

    public static FeatureDto populate(String name, String dockerData, String testInstruction, String description, boolean approved, List<TagDto> tags) {
        final FeatureDto result = new FeatureDto();
        result.name = name;
        result.dockerData = dockerData;
        result.testInstruction = testInstruction;
        result.approved = approved;
        result.description = description;
        result.tags = tags;
        return result;
    }

    public static FeatureDto populate(String name, String dockerData, String testInstruction, String description, boolean approved) {
        return populate(name, dockerData, testInstruction, description, approved, Collections.emptyList());
    }

    protected static void fillShort(Feature feature, FeatureDto result) {
        result.name = feature.getName();
        result.dockerData = feature.getDockerData();
        result.testInstruction = feature.getTestInstruction();
        result.description = feature.getDescription();
        result.approved = feature.isApproved();
        if (null != feature.getTags() && !feature.getTags().isEmpty()) {
            result.tags = feature.getTags().stream().map(TagDto::ofShort).collect(Collectors.toList());
        }
        if (feature.getFiles() != null) {
            result.files = feature.getFiles().stream().map(FileDto::fromFile).collect(Collectors.toSet());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDockerData() {
        return dockerData;
    }

    public void setDockerData(String dockerData) {
        this.dockerData = dockerData;
    }

    public String getTestInstruction() {
        return testInstruction;
    }

    public void setTestInstruction(String testInstruction) {
        this.testInstruction = testInstruction;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TagDto> getTags() {
        return (null != tags) ? tags : Collections.emptyList();
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public Set<FileDto> getFiles() {
        return files;
    }

    public void setFiles(Set<FileDto> files) {
        this.files = files;
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

    @Override
    public String toString() {
        return "FeatureDto{" +
                "name='" + name + '\'' +
                ", dockerData='" + dockerData + '\'' +
                ", testInstruction='" + testInstruction + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", files='" + description + '\'' +
                '}';
    }
}
