package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zalando.pazuzu.exception.*;

import javax.persistence.criteria.Predicate;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    @Autowired
    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    private static void collectRecursively(Collection<Feature> result, Feature f) {
        result.add(f);
        f.getDependencies().forEach(item -> collectRecursively(result, item));
    }

    @Transactional(rollbackFor = ServiceException.class)
    public <T> T createFeature(String name, String description, String author, String snippet, String testSnippet,
                               List<String> dependencyNames, Function<Feature, T> converter) {
        final Feature newFeature = new Feature();

        setFeatureName(name, newFeature);
        createDependencies(dependencyNames, newFeature);
        if (null != snippet && !snippet.isEmpty()) {
            newFeature.setSnippet(snippet);
        }
        if (null != testSnippet && !testSnippet.isEmpty()) {
            newFeature.setTestSnippet(testSnippet);
        }
        if (null != description && !description.isEmpty()) {
            newFeature.setDescription(description);
        }
        if (null != author && !author.isEmpty()) {
            newFeature.setAuthor(author);
        }
        newFeature.setStatus(FeatureStatus.PENDING);
        featureRepository.save(newFeature);
        return converter.apply(newFeature);
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
    public <T> T updateFeature(String name, String newName, String description, String author, String snippet,
                               String testSnippet, List<String> dependencyNames, FeatureStatus status,
                               Function<Feature, T> converter) {
        final Feature existing = loadExistingFeature(name);

        if (null != newName && !newName.equals(existing.getName())) {
            setFeatureName(newName, existing);
        }
        // Allow deleting nullable data
        existing.setSnippet(valueOrNull(snippet));
        existing.setTestSnippet(valueOrNull(testSnippet));
        existing.setDescription(valueOrNull(description));
        existing.setAuthor(valueOrNull(author));
        existing.setStatus(status);

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

    private String valueOrNull(String value) {
        return null != value && !value.isEmpty() ? value : null;
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

    public Set<Feature> loadFeatures(List<String> featureNames) throws ServiceException {
        if (featureNames == null || featureNames.isEmpty())
            return Collections.emptySet();  // no need to go to database for this

        final Set<String> uniqueFeatureNames = new HashSet<>(
                featureNames.stream().map(t -> t.toLowerCase()).collect(Collectors.toList()));
        Specification<Feature> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (uniqueFeatureNames.size() > 1)
                predicates.add(builder.lower(root.get(Feature_.name)).in(uniqueFeatureNames));
            else
                uniqueFeatureNames.stream().findFirst().map(fn ->
                    predicates.add(builder.equal(builder.lower(root.get(Feature_.name)), fn))
                );

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        final Collection<Feature> foundFeatures = featureRepository.findAll(spec);

        if (foundFeatures.size() != uniqueFeatureNames.size()) {
            final Set<String> missingFeaturesNames = new HashSet<>(uniqueFeatureNames);
            foundFeatures.forEach(f -> missingFeaturesNames.remove(f.getName()));
            throw new FeatureNotFoundException("Feature missing: " + String.join(",", missingFeaturesNames));
        }

        return new HashSet<>(foundFeatures);
    }


    /**
     * Search feature base on give search criteria, ordered by name.
     * @param name string the name should contains.
     * @param author string the author should contains.
     * @param status the status of the feature, if not present do no filter on status.
     * @param offset the offset of the result list (must be present)
     * @param limit the maximum size of the returned list (must be present)
     * @param converter the converter that will be used to map the feature to the expected result type
     * @param <T> the list item result type
     * @return paginated list of feature that match the search criteria with the totcal count.
     * @throws ServiceException
     */
    @Transactional
    public <T> FeaturesPage<Feature, T> searchFeatures(String name, String author, FeatureStatus status,
                                              Integer offset, Integer limit,
                                              Function<Feature, T> converter) throws ServiceException {
        Specification<Feature> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(builder.like(builder.lower(root.get(Feature_.name)), escapeLike(name), '|' ));
            }

            if (author != null) {
                predicates.add(builder.like(builder.lower(root.get(Feature_.author)), escapeLike(author), '|'));
            }

            if (status != null) {
                predicates.add(builder.equal(root.get(Feature_.status), status));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable pageable = new PageRequest(offset / limit, limit, Sort.Direction.ASC, "name");
        Page<Feature> page = featureRepository.findAll(spec, pageable);
        return new FeaturesPage<>(page, converter);

    }

    private String escapeLike(String name) {
        return "%" + name.replace("%", "|%").toLowerCase(Locale.ENGLISH) + "%";
    }

    public List<Feature> resolveFeatures(List<String> featureNames) {
        final Set<Feature> expandedList = new HashSet<>();
        loadFeatures(featureNames).forEach(f -> collectRecursively(expandedList, f));
        return new ArrayList<>(expandedList);
    }

    private Feature loadExistingFeature(String name) throws FeatureNotFoundException {
        return featureRepository.findByName(name)
                .orElseThrow(() -> new FeatureNotFoundException("Feature missing: " + name));
    }
}
