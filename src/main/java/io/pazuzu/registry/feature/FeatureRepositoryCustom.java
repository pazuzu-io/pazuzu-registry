package io.pazuzu.registry.feature;

import java.util.List;
import io.pazuzu.registry.domain.Feature;


public interface FeatureRepositoryCustom {
    List<Feature> getFeatures(int offset, int limit);
}
