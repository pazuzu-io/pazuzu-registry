package io.pazuzu.registry.api;

import io.pazuzu.registry.model.Feature;
import io.pazuzu.registry.model.FeatureMeta;
import io.pazuzu.registry.model.Review;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Util class use to convert feature entity to JSON dto.
 */
public class FeatureConverter {

    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
    };

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
            dto.setReviewStatus(Review.ReviewStatusEnum.valueOf(feature.getStatus().jsonValue()));
        return dto;
    }

    public static FeatureMeta asMetaDto(io.pazuzu.registry.feature.Feature feature) {
        FeatureMeta dto = new FeatureMeta();
        dto.setName(feature.getName());
        dto.setDescription(feature.getDescription());
        dto.setAuthor(feature.getAuthor());
        dto.setUpdatedAt(dateFormat.get().format(feature.getUpdatedAt()));
        dto.setCreatedAt(dateFormat.get().format(feature.getCreatedAt()));
        dto.setStatus(FeatureMeta.StatusEnum.valueOf(feature.getStatus().jsonValue()));
        dto.setDependencies(
                feature.getDependencies().stream().map(
                        io.pazuzu.registry.feature.Feature::getName).collect(Collectors.toList()));
        return dto;
    }

    /**
     * Return the feature converter for the provided feature fields.
     * @param featureFields the fields that should be return
     * @return the feature converter
     */
    public static Function<io.pazuzu.registry.feature.Feature, Feature> forFields(FeatureFields featureFields) {
        return f -> asDto(f, featureFields);
    }
}
