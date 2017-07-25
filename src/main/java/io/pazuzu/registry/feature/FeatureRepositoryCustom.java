package io.pazuzu.registry.feature;

import java.util.List;

public interface FeatureRepositoryCustom {
    List<Feature> getFeatures(int offset, int limit);
}
