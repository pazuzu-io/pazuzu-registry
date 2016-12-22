package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeatureMetaDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("author")
    private String author;
    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
    @JsonProperty("dependencies")
    private List<String> dependencies = new ArrayList<>();

    public String getName() {
        return name;
    }

    public FeatureMetaDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public FeatureMetaDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public FeatureMetaDto setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public FeatureMetaDto setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public FeatureMetaDto setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
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
        FeatureMetaDto that = (FeatureMetaDto) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(author, that.author) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
