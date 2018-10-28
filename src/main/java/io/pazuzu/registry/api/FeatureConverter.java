package io.pazuzu.registry.api;

import io.pazuzu.registry.model.Feature;
import io.pazuzu.registry.model.FeatureMeta;
import io.pazuzu.registry.model.Review;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Util class use to convert feature entity to JSON dto.
 */
public class FeatureConverter {

    public static Feature asDto(io.pazuzu.registry.feature.Feature feature) {
        return asDto(feature, FeatureFields.ALL);
    }

    public static Feature asDto(io.pazuzu.registry.feature.Feature feature, FeatureFields featureFields) {
        Feature dto = new Feature();
        dto.setMeta(asMetaDto(feature));
        if (featureFields.equals(FeatureFields.ALL)) {
            dto.setSnippet(feature.getSnippet());
            dto.setTestSnippet(feature.getTestSnippet());
        }
        return dto;
    }

    public static Review asReviewDto(io.pazuzu.registry.feature.Feature feature) {
        Review dto = new Review();
        if (feature.getStatus() != null)
            dto.setReviewStatus(Review.ReviewStatus.valueOf(feature.getStatus().jsonValue()));
        return dto;
    }

    public static FeatureMeta asMetaDto(io.pazuzu.registry.feature.Feature feature) {
        FeatureMeta dto = new FeatureMeta();
        dto.setName(feature.getName());
        dto.setDescription(feature.getDescription());
        dto.setAuthor(feature.getAuthor());
        dto.setUpdatedAt(feature.getUpdatedAt());
        dto.setCreatedAt(feature.getCreatedAt());
        dto.setStatus(FeatureMeta.FeatureStatus.valueOf(feature.getStatus().jsonValue()));
        dto.setDependencies(
                feature.getDependencies().stream().map(
                        io.pazuzu.registry.feature.Feature::getName).collect(Collectors.toList()));
        return dto;
    }

    /**
     * Return the feature converter for the provided feature fields.
     *
     * @param featureFields the fields that should be return
     * @return the feature converter
     */
    public static Function<io.pazuzu.registry.feature.Feature, Feature> forFields(FeatureFields featureFields) {
        return f -> asDto(f, featureFields);
    }
}
