package org.zalando.pazuzu.feature;

import java.util.List;

public interface FeatureRepositoryCustom {
    public List<Feature> getFeatures(int offset, int limit);
}
