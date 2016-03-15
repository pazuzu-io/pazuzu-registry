package pazuzu.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pazuzu.dao.ContainerRepository;
import pazuzu.dao.FeatureRepository;
import pazuzu.model.Container;
import pazuzu.model.Feature;

@Service
public class ContainerService {
    private final ContainerRepository containerRepository;
    private final FeatureRepository featureRepository;
    private final FeatureService featureService;

    @Inject
    public ContainerService(ContainerRepository containerRepository, FeatureRepository featureRepository, FeatureService featureService) {
        this.containerRepository = containerRepository;
        this.featureRepository = featureRepository;
        this.featureService = featureService;
    }

    public <T> List<T> listContainers(String name, Function<Container, T> converter) {
        return containerRepository.findByNameIgnoreCaseContaining(name).stream().map(converter).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T createContainer(String name, List<String> features, Function<Container, T> converter) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new ServiceException("name", "Feature name is empty");
        }
        ensureNameFree(name);
        final Container container = new Container();
        container.setName(name);
        container.setFeatures(featureService.loadFeatures(features));
        containerRepository.save(container);
        return converter.apply(container);
    }

    private void ensureNameFree(String name) throws ServiceException {
        final Container existing = containerRepository.findByName(name);
        if (null != existing) {
            throw new ServiceException("duplicate", "Feature with name " + name + " already exists");
        }
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T updateContainer(String containerName, String newName, List<String> features, Function<Container, T> converter) throws ServiceException {
        final Container container = containerRepository.findByName(containerName);
        if (null == container) {
            throw new ServiceException.NotFoundException("not_found", "Container with name " + containerName + " is not found");
        }
        if (null != newName) {
            ensureNameFree(newName);
            container.setName(newName);
        }
        if (null != features) {
            container.setFeatures(featureService.loadFeatures(features));
        }
        containerRepository.save(container);
        return converter.apply(container);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T getContainer(String containerName, Function<Container, T> converter) throws ServiceException {
        final Container container = getContainer(containerName);
        return converter.apply(container);
    }

    private Container getContainer(String containerName) throws ServiceException {
        final Container container = containerRepository.findByName(containerName);
        if (null == container) {
            throw new ServiceException.NotFoundException("not_found", "Container with name is not found");
        }
        return container;
    }

    @Transactional
    public void deleteContainer(String containerName) {
        Optional.ofNullable(containerRepository.findByName(containerName)).ifPresent(containerRepository::delete);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T addFeature(String containerName, String featureName, Function<Container, T> converter) throws ServiceException {
        final Container container = getContainer(containerName);
        final Feature feature = featureRepository.findByName(featureName);
        if (null == feature) {
            throw new ServiceException("bad_feature", "Feature with name " + featureName + " is not found");
        }
        container.getFeatures().add(feature);
        containerRepository.save(container);
        return converter.apply(container);
    }
}
