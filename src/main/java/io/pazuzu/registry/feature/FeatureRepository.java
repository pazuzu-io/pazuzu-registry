package io.pazuzu.registry.feature;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import io.pazuzu.registry.domain.Feature;

public interface FeatureRepository extends CrudRepository<Feature, Integer>, FeatureRepositoryCustom, JpaSpecificationExecutor<Feature> {
    Optional<Feature> findByName(String name);

    Set<Feature> findByNameIn(Set<String> name);

    List<Feature> findByDependenciesContaining(Feature feature);
}
