package io.pazuzu.registry.model;

import java.util.List;

public class FeatureList {
    private int totalCount;

    private List<Feature> features;

    private FeatureListLinks _links;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public FeatureListLinks getLinks() {
        return _links;
    }

    public void setLinks(FeatureListLinks links) {
        this._links = links;
    }

    public FeatureList() {
    }

    public FeatureList(int totalCount, List<Feature> features, FeatureListLinks _links) {
        this.totalCount = totalCount;
        this.features = features;
        this._links = _links;
    }
}
