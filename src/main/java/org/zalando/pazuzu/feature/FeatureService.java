package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zalando.pazuzu.container.Container;
import org.zalando.pazuzu.container.ContainerRepository;
import org.zalando.pazuzu.docker.DockerfileUtil;
import org.zalando.pazuzu.exception.BadRequestException;
import org.zalando.pazuzu.exception.Error;
import org.zalando.pazuzu.exception.NotFoundException;
import org.zalando.pazuzu.exception.ServiceException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public FeatureService(FeatureRepository featureRepository,
                          ContainerRepository containerRepository) {
        this.featureRepository = featureRepository;
        this.containerRepository = containerRepository;
    }

    @Transactional
    public <T> List<T> listFeatures(String name, Function<Feature, T> converter) {
        return featureRepository.findByNameIgnoreCaseContaining(name).stream().map(converter).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T createFeature(String name, String dockerData, List<String> dependencyNames, Function<Feature, T> converter) throws ServiceException {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(Error.FEATURE_NAME_EMPTY);
        }
        final Feature existing = featureRepository.findByName(name);
        if (null != existing) {
            throw new BadRequestException(Error.FEATURE_DUPLICATE);
        }

        final Set<Feature> dependencies = loadFeatures(dependencyNames);

        final Feature newFeature = new Feature();
        newFeature.setName(name);
        newFeature.setDockerData(null == dockerData ? "" : dockerData);
        newFeature.setDependencies(dependencies);
        featureRepository.save(newFeature);
        return converter.apply(newFeature);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T updateFeature(String name, String newName, String dockerData, List<String> dependencyNames, Function<Feature, T> converter) throws ServiceException {
        final Feature existing = loadExistingFeature(name);
        if (null != newName && !newName.equals(existing.getName())) {
            final Feature newExisting = featureRepository.findByName(newName);
            if (null != newExisting) {
                throw new BadRequestException(Error.FEATURE_DUPLICATE);
            }
            existing.setName(newName);
        }
        if (null != dockerData) {
            existing.setDockerData(dockerData);
        }
        if (null != dependencyNames) {
            final Set<Feature> dependencies = loadFeatures(dependencyNames);
            final List<Feature> recursive = dependencies.stream()
                    .filter(f -> f.containsDependencyRecursively(existing)).collect(Collectors.toList());
            if (!recursive.isEmpty()) {
                throw new BadRequestException(Error.FEATURE_HAS_RECURSIVE_DEPENDENCY);
            }
            existing.setDependencies(dependencies);
        }
        featureRepository.save(existing);
        return converter.apply(existing);
    }

    @Transactional
    public <T> T getFeature(String featureName, Function<Feature, T> converter) throws ServiceException {
        return converter.apply(loadExistingFeature(featureName));
    }

    @Transactional(rollbackFor = ServiceException.class)
    public void deleteFeature(String featureName) throws ServiceException {
        final Feature feature = featureRepository.findByName(featureName);
        if (feature == null) {
            throw new NotFoundException(Error.FEATURE_NOT_FOUND);
        }
        final List<Feature> referencing = featureRepository.findByDependenciesContaining(feature);
        if (!referencing.isEmpty()) {
            throw new BadRequestException(Error.FEATURE_NOT_DELETABLE_DUE_TO_REFERENCES);
        }
        final List<Container> referencingContainers = containerRepository.findByFeaturesContaining(feature);
        if (!referencingContainers.isEmpty()) {
            throw new BadRequestException(Error.FEATURE_NOT_DELETABLE_DUE_TO_REFERENCES);
        }
        featureRepository.delete(feature);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public String generateDockerfile(List<String> features) throws ServiceException {
        return DockerfileUtil.generateDockerfile(Optional.empty(), loadFeatures(features));
    }

    public Set<Feature> loadFeatures(List<String> dependencyNames) throws ServiceException {
        final Set<String> uniqueDependencies = null == dependencyNames ? new HashSet<>() : new HashSet<>(dependencyNames);
        final Set<Feature> dependencies = uniqueDependencies.stream()
                .map(featureRepository::findByName).filter(f -> f != null)
                .collect(Collectors.toSet());
        if (dependencies.size() != uniqueDependencies.size()) {
            dependencies.forEach(f -> uniqueDependencies.remove(f.getName()));
            throw new BadRequestException(Error.FEATURE_NOT_FOUND);
        }
        return dependencies;
    }

    private Feature loadExistingFeature(String name) throws NotFoundException {
        final Feature existing = featureRepository.findByName(name);
        if (null == existing) {
            throw new NotFoundException(Error.FEATURE_NOT_FOUND);
        }
        return existing;
    }
}
