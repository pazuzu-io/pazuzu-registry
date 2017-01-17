package org.zalando.pazuzu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.pazuzu.feature.Feature;

import java.util.Objects;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureDto {
    @JsonProperty("meta")
    private FeatureMetaDto meta = new FeatureMetaDto();
    @JsonProperty("snippet")
    private String snippet;
    @JsonProperty("test_snippet")
    private String testSnippet;

    public FeatureDto() {
    }

    public FeatureDto(String name) {
        this.getMeta().setName(name);
    }

    public static FeatureDto of(Feature feature) {

        FeatureDto dto = new FeatureDto()
                .setSnippet(feature.getSnippet())
                .setTestSnippet(feature.getTestSnippet());
        dto.getMeta().setName(feature.getName())
                .setDescription(feature.getDescription())
                .setAuthor(feature.getAuthor())
                .setUpdatedAt(feature.getUpdatedAt())
                .setDependencies(feature.getDependencies().stream().map(Feature::getName).collect(Collectors.toList()));
        return dto;
    }

    public FeatureMetaDto getMeta() {
        return meta;
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

    @Override
    public String toString() {
        return "FeatureDto{" +
                "meta=" + meta +
                ", snippet='" + snippet + '\'' +
                ", testSnippet='" + testSnippet + '\'' +
                '}';
    }
}
