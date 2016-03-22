package org.zalando.pazuzu.container;

import org.springframework.data.repository.Repository;
import org.zalando.pazuzu.feature.Feature;

import java.util.List;

public interface ContainerRepository extends Repository<Container, Integer> {

    List<Container> findByNameIgnoreCaseContaining(String name);

    Container findByName(String name);

    Container save(Container container);

    void delete(Container container);

    List<Container> findByFeaturesContaining(Feature feature);
}
