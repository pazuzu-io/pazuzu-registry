package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zalando.pazuzu.exception.BadRequestException;
import org.zalando.pazuzu.exception.NotFoundException;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.feature.file.DockerParser;
import org.zalando.pazuzu.feature.file.FileService;
import org.zalando.pazuzu.feature.tag.TagDto;
import org.zalando.pazuzu.feature.tag.TagService;
import org.zalando.pazuzu.sort.TopologicalSortLinear;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final TagService tagService;
    private final FileService fileService;

    @Autowired
    public FeatureService(FeatureRepository featureRepository, TagService tagService, FileService fileService) {
        this.featureRepository = featureRepository;
        this.tagService = tagService;
        this.fileService = fileService;
    }

    private static void collectRecursively(Collection<Feature> result, Feature f) {
        result.add(f);
        f.getDependencies().forEach(item -> collectRecursively(result, item));
    }

    @Transactional
    public <T> List<T> listFeatures(String name, Function<Feature, T> converter) {
        return this.featureRepository.findByNameIgnoreCaseContaining(name).stream().map(converter).collect(Collectors.toList());
    }

    @Transactional
    public <T> FeaturesWithTotalCount<T> getFeaturesWithTotalCount(int offset, int limit, Function<Feature, T> converter) {
        List<T> features = featureRepository.getFeatures(offset, limit).stream().map(converter).collect(Collectors.toList());
        long count = featureRepository.count();
        return new FeaturesWithTotalCount<>(features, count);
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T createFeature(String name, String dockerData, String testInstruction, String description,
                               List<String> dependencyNames, List<TagDto> tags, Function<Feature, T> converter) throws ServiceException {
        final Feature newFeature = new Feature();
        createName(name, newFeature);
        createDependencies(dependencyNames, newFeature);

        setDockerData(newFeature, dockerData);
        if (null != testInstruction) {
            newFeature.setTestInstruction(testInstruction);
        }
        if (null != description && !description.isEmpty()) {
            newFeature.setDescription(description);
        }
        if (null != tags && !tags.isEmpty()) {
            newFeature.setTags(tagService.upsertTagDtos(tags));
        }
        featureRepository.save(newFeature);
        return converter.apply(newFeature);
    }

    private void setDockerData(Feature feature, String dockerData) {
        feature.setDockerData(null == dockerData ? "" : dockerData);
    }

    private void createName(String name, Feature newFeature) throws BadRequestException {
        nameGuardCheck(name);
        newFeature.setName(name);
    }

    private void createDependencies(List<String> dependencyNames, Feature newFeature) throws ServiceException {
        final Set<Feature> dependencies = loadFeatures(dependencyNames);

        newFeature.setDependencies(dependencies);
    }

    private void nameGuardCheck(String name) throws BadRequestException {
        if (StringUtils.isEmpty(name)) {
            throw new BadRequestException(FeatureErrors.FEATURE_NAME_EMPTY);
        }
        final Feature existing = featureRepository.findByName(name);
        if (null != existing) {
            throw new BadRequestException(FeatureErrors.FEATURE_DUPLICATE);
        }
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T updateFeature(String name, String newName, String dockerData, String testInstruction,
                               String description, List<String> dependencyNames, Function<Feature, T> converter) throws ServiceException {
        final Feature existing = loadExistingFeature(name);
        if (null != newName && !newName.equals(existing.getName())) {
            final Feature newExisting = featureRepository.findByName(newName);
            if (null != newExisting) {
                throw new BadRequestException(FeatureErrors.FEATURE_DUPLICATE);
            }
            existing.setName(newName);
        }

        setDockerData(existing, dockerData);

        if (null != testInstruction) {
            existing.setTestInstruction(testInstruction);
        }
        if (null != description) {
            existing.setDescription(description);
        }
        if (null != dependencyNames) {
            final Set<Feature> dependencies = loadFeatures(dependencyNames);
            final List<Feature> recursive = dependencies.stream()
                    .filter(f -> f.containsDependencyRecursively(existing)).collect(Collectors.toList());
            if (!recursive.isEmpty()) {
                throw new BadRequestException(FeatureErrors.FEATURE_HAS_RECURSIVE_DEPENDENCY,
                        "Recursive dependencies found: " + recursive.stream().map(Feature::getName).collect(Collectors.joining(", ")));
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
            throw new NotFoundException(FeatureErrors.FEATURE_NOT_FOUND);
        }
        final List<Feature> referencing = featureRepository.findByDependenciesContaining(feature);
        if (!referencing.isEmpty()) {
            throw new BadRequestException(FeatureErrors.FEATURE_NOT_DELETABLE_DUE_TO_REFERENCES,
                    "Can't delete feature because it is referenced from other feature(s): "
                            + referencing.stream().map(Feature::getName).collect(Collectors.joining(", ")));
        }
        featureRepository.delete(feature);
    }

    public Set<Feature> loadFeatures(List<String> dependencyNames) throws ServiceException {
        final Set<String> uniqueDependencies = null == dependencyNames ? new HashSet<>() : new HashSet<>(dependencyNames);
        final Set<Feature> dependencies = uniqueDependencies.stream()
                .map(featureRepository::findByName).filter(f -> f != null)
                .collect(Collectors.toSet());
        if (dependencies.size() != uniqueDependencies.size()) {
            dependencies.forEach(f -> uniqueDependencies.remove(f.getName()));
            throw new BadRequestException(FeatureErrors.FEATURE_NOT_FOUND);
        }
        return dependencies;
    }

    public List<Feature> getSortedFeatures(Collection<Feature> features) {
        final Set<Feature> expandedList = new HashSet<>();
        features.forEach(f -> collectRecursively(expandedList, f));
        return new TopologicalSortLinear<>(expandedList, Feature::getDependencies).getTopSorted();
    }

    private Feature loadExistingFeature(String name) throws NotFoundException {
        final Feature existing = featureRepository.findByName(name);
        if (null == existing) {
            throw new NotFoundException(FeatureErrors.FEATURE_NOT_FOUND);
        }
        return existing;
    }
}
