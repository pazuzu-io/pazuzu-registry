package org.zalando.pazuzu.dao;

import java.util.List;
import org.springframework.data.repository.Repository;
import org.zalando.pazuzu.model.Feature;

public interface FeatureRepository extends Repository<Feature, Integer> {
    Feature save(Feature value);

    List<Feature> findByNameIgnoreCaseContaining(String name);

    Feature findByName(String name);

    List<Feature> findByDependenciesContaining(Feature feature);

    void delete(Feature feature);
}
