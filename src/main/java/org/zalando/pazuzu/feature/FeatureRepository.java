package org.zalando.pazuzu.feature;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface FeatureRepository extends Repository<Feature, Integer> {
    Feature save(Feature value);

    List<Feature> findByNameIgnoreCaseContaining(String name);

    Feature findByName(String name);

    List<Feature> findByDependenciesContaining(Feature feature);

    void delete(Feature feature);
}
