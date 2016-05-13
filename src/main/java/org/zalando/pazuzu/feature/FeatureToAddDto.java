package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FeatureToAddDto {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureToAddDto that = (FeatureToAddDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
