package org.zalando.pazuzu.container;

import org.springframework.data.repository.CrudRepository;
import org.zalando.pazuzu.feature.Feature;

import java.util.List;

public interface ContainerRepository extends CrudRepository<Container, Integer> {

    List<Container> findByNameIgnoreCaseContaining(String name);

    Container findByName(String name);

    List<Container> findByFeaturesContaining(Feature feature);
}
