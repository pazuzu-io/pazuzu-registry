package org.zalando.pazuzu.container;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zalando.pazuzu.docker.DockerfileUtil;
import org.zalando.pazuzu.exception.BadRequestException;
import org.zalando.pazuzu.exception.NotFoundException;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.feature.Feature;
import org.zalando.pazuzu.feature.FeatureRepository;
import org.zalando.pazuzu.feature.FeatureService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final FeatureRepository featureRepository;
    private final FeatureService featureService;

    @Autowired
    public ContainerService(ContainerRepository containerRepository,
                            FeatureRepository featureRepository,
                            FeatureService featureService) {
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
            throw new BadRequestException("name", "Feature name is empty");
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
            throw new BadRequestException("duplicate", "Feature with name " + name + " already exists");
        }
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T updateContainer(String containerName, String newName, List<String> features, Function<Container, T> converter) throws ServiceException {
        final Container container = containerRepository.findByName(containerName);
        if (null == container) {
            throw new NotFoundException("not_found", "Container with name " + containerName + " is not found");
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

    @Transactional(rollbackFor = ServiceException.class)
    public Container getContainer(String containerName) throws ServiceException {
        final Container container = containerRepository.findByName(containerName);
        if (null == container) {
            throw new NotFoundException("not_found", "Container with name is not found");
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
            throw new BadRequestException("bad_feature", "Feature with name " + featureName + " is not found");
        }
        container.getFeatures().add(feature);
        containerRepository.save(container);
        return converter.apply(container);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public void deleteFeature(String containerName, String featureName) throws ServiceException {
        final Container container = getContainer(containerName);
        final Feature toDelete = container.getFeatures().stream().filter(f -> f.getName().equals(featureName)).findAny().orElse(null);
        if (null != toDelete) {
            container.getFeatures().remove(toDelete);
            containerRepository.save(container);
        }
    }

    @Transactional(rollbackFor = ServiceException.class)
    public String generateDockerfile(String containerName) throws ServiceException {
        Container container = getContainer(containerName);
        return DockerfileUtil.generateDockerfile(Optional.of(container.getName()), container.getFeatures());
    }
}
