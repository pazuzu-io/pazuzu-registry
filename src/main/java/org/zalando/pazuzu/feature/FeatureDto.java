package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.pazuzu.feature.tag.TagDto;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FeatureDto {
    @JsonProperty("meta")
    private FeatureMetaDto meta = new FeatureMetaDto();
    @JsonProperty("snippet")
    private String snippet;
    @JsonProperty("test_snippet")
    private String testSnippet;

    public static FeatureDto of(Feature feature) {

        FeatureDto dto = new FeatureDto()
                .setSnippet(feature.getDockerData())
                .setTestSnippet(feature.getTestInstruction());
        dto.getMeta().setName(feature.getName())
                .setDescription(feature.getDescription())
                .setDependencies(feature.getDependencies().stream().map(Feature::getName).collect(Collectors.toList()));
        return dto;
    }

    public FeatureMetaDto getMeta() {
        return meta;
    }

    public FeatureDto setMeta(FeatureMetaDto meta) {
        this.meta = meta;
        return this;
    }

    public String getSnippet() {
        return snippet;
    }

    public FeatureDto setSnippet(String snippet) {
        this.snippet = snippet;
        return this;
    }

    public String getTestSnippet() {
        return testSnippet;
    }

    public FeatureDto setTestSnippet(String testSnippet) {
        this.testSnippet = testSnippet;
        return this;
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
        return Objects.equals(meta, that.meta) &&
                Objects.equals(snippet, that.snippet) &&
                Objects.equals(testSnippet, that.testSnippet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meta, snippet, testSnippet);
    }
}
