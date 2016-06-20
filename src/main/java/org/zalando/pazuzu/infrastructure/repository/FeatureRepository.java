package org.zalando.pazuzu.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import org.zalando.pazuzu.infrastructure.domain.Feature;

import java.util.List;

public interface FeatureRepository extends CrudRepository<Feature, Integer>, FeatureRepositoryCustom {

    List<Feature> findByNameIgnoreCaseContaining(String name);

    Feature findByName(String name);

    List<Feature> findByDependenciesContaining(Feature feature);
}
