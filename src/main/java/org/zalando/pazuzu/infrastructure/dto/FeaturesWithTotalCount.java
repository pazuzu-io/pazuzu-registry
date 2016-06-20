package org.zalando.pazuzu.infrastructure.dto;

import java.util.List;

public class FeaturesWithTotalCount<T> {
    private final List<T> features;
    private final long totalCount;

    public FeaturesWithTotalCount(List<T> features, long totalCount) {
        this.features = features;
        this.totalCount = totalCount;
    }

    public List<T> getFeatures() {
        return features;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
