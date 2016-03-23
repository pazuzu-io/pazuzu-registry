package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureFullDto extends FeatureDto {

    @JsonProperty("dependencies")
    private List<FeatureDto> dependencies;

    public List<FeatureDto> getDependencies() {
        if (null == dependencies) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }

    public static FeatureFullDto makeFull(Feature feature) {
        if (null == feature) {
            return null;
        }
        final FeatureFullDto result = new FeatureFullDto();
        fillShort(feature, result);
        result.dependencies = feature.getDependencies().stream().map(FeatureDto::ofShort).collect(Collectors.toList());
        return result;
    }
}
