package pazuzu.dao;

import java.util.List;
import org.springframework.data.repository.Repository;
import pazuzu.model.Container;
import pazuzu.model.Feature;

public interface ContainerRepository extends Repository<Container, Integer> {
    List<Container> findByNameIgnoreCaseContaining(String name);

    Container findByName(String name);

    Container save(Container container);

    void delete(Container container);

    List<Container> findByFeaturesContaining(Feature feature);
}
