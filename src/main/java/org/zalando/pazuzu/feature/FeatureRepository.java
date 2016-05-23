package org.zalando.pazuzu.feature;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeatureRepository extends CrudRepository<Feature, Integer>, FeatureRepositoryCustom {

    List<Feature> findByNameIgnoreCaseContaining(String name);

    Feature findByName(String name);

    List<Feature> findByDependenciesContaining(Feature feature);
}
