package org.zalando.pazuzu.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeatureToCreateDto extends FeatureDto {

    @JsonProperty("dependencies")
    private List<String> dependencies;

    public List<String> getDependencies() {
        if (null == dependencies) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        FeatureToCreateDto that = (FeatureToCreateDto) o;
        return Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dependencies);
    }
}
