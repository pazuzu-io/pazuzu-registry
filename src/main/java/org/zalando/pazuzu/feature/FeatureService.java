package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zalando.pazuzu.exception.*;
import org.zalando.pazuzu.feature.file.DockerParser;
import org.zalando.pazuzu.feature.file.FileService;
import org.zalando.pazuzu.feature.tag.TagDto;
import org.zalando.pazuzu.feature.tag.TagService;
import org.zalando.pazuzu.sort.TopologicalSortLinear;

import javax.ws.rs.BadRequestException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

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
                               List<String> dependencyNames, Function<Feature, T> converter) {
        final Feature newFeature = new Feature();

        setFeatureName(name, newFeature);
        createDependencies(dependencyNames, newFeature);
        setDockerData(newFeature, dockerData);
        newFeature.setApproved(false);

        if (null != testInstruction) {
            newFeature.setTestInstruction(testInstruction);
        }
        if (null != description && !description.isEmpty()) {
            newFeature.setDescription(description);
        }
        featureRepository.save(newFeature);
        return converter.apply(newFeature);
    }

    private void setDockerData(Feature feature, String dockerData) {
        // TODO (usability) This might be too strict and we should allow saving incomplete features.
        // TODO (usability) Report list of missing files.
        feature.setDockerData(null == dockerData ? "" : dockerData);
        updateFileLinks(feature);
    }

    private void setFeatureName(String name, Feature feature) throws BadRequestException {
        nameGuardCheck(name);
        feature.setName(name);
    }

    private void createDependencies(List<String> dependencyNames, Feature newFeature) {
        final Set<Feature> dependencies = loadFeatures(dependencyNames);

        newFeature.setDependencies(dependencies);
    }

    private void nameGuardCheck(String name) throws BadRequestException {
        if (StringUtils.isEmpty(name)) {
            throw new FeatureNameEmptyException();
        }

        featureRepository.findByName(name).ifPresent(f -> {
            throw new FeatureDuplicateException(String.format("Feature with name %s already exists", f.getName()));
        });
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T updateFeature(String name, String newName, String dockerData, String testInstruction,
                               String description, List<String> dependencyNames, Function<Feature, T> converter) {
        final Feature existing = loadExistingFeature(name);

        if (null != newName && !newName.equals(existing.getName())) {
            setFeatureName(newName, existing);
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
                throw new FeatureRecursiveDependencyException(
                        "Recursive dependencies found: " + recursive.stream().map(Feature::getName).collect(Collectors.joining(", ")));
            }
            existing.setDependencies(dependencies);
        }
        featureRepository.save(existing);
        return converter.apply(existing);
    }

    private void updateFileLinks(Feature feature) {
        feature.setFiles(
                DockerParser.getCopyFiles(feature.getDockerData())
                        .stream().map(fileService::getByName)
                        .collect(toSet()));
    }

    @Transactional
    public <T> T getFeature(String featureName, Function<Feature, T> converter) throws ServiceException {
        return converter.apply(loadExistingFeature(featureName));
    }

    @Transactional(rollbackFor = ServiceException.class)
    public void deleteFeature(String featureName) throws ServiceException {
        final Feature feature = loadExistingFeature(featureName);
        final List<Feature> referencing = featureRepository.findByDependenciesContaining(feature);
        if (!referencing.isEmpty()) {
            throw new FeatureReferencedDeleteException(
                    "Can't delete feature because it is referenced from other feature(s): "
                            + referencing.stream().map(Feature::getName).collect(Collectors.joining(", ")));
        }
        featureRepository.delete(feature);
    }

    @Transactional
    public void approveFeature(String featureName) throws ServiceException {
        Feature feature = loadExistingFeature(featureName);
        feature.setApproved(true);
        featureRepository.save(feature);
    }

    public Set<Feature> loadFeatures(List<String> featureNames) throws ServiceException {
        final Set<String> uniqueFeatureNames = null == featureNames ? new HashSet<>() : new HashSet<>(featureNames);
        final Set<Feature> foundFeatures = featureRepository.findByNameIn(uniqueFeatureNames);

        if (foundFeatures.size() != uniqueFeatureNames.size()) {
            final Set<String> missingFeaturesNames = new HashSet<>(uniqueFeatureNames);
            foundFeatures.forEach(f -> missingFeaturesNames.remove(f.getName()));
            throw new FeatureNotFoundException("Feature missing: " + String.join(",", missingFeaturesNames));
        }

        return foundFeatures;
    }

    public List<Feature> getSortedFeatures(Collection<Feature> features) {
        final Set<Feature> expandedList = new HashSet<>();
        features.forEach(f -> collectRecursively(expandedList, f));
        return new TopologicalSortLinear<>(expandedList, Feature::getDependencies).getTopSorted();
    }

    private Feature loadExistingFeature(String name) throws FeatureNotFoundException {
        return featureRepository.findByName(name)
                .orElseThrow(() -> new FeatureNotFoundException("Feature missing: " + name));
    }
}
