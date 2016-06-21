package org.zalando.pazuzu.infrastructure.repository;

import org.zalando.pazuzu.infrastructure.domain.Feature;

import java.util.List;

public interface FeatureRepositoryCustom {

    List<Feature> getFeatures(int offset, int limit);
}
