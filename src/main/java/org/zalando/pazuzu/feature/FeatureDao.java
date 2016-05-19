package org.zalando.pazuzu.feature;

import java.util.List;

interface FeatureDao {
    List<Feature> getFeatures(int offset, int limit);
}