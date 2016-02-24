package pazuzu.service;

import java.util.Collection;
import java.util.List;

import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
public interface FeatureService {

    /**
     * Get a List of all available features
     *
     * @return List of all available features
     */
    Collection<Feature> getAllFeatures();

    /**
     * Creates a topologically sorted List of Features
     *
     * @param featureNames List of all required featurenames
     * @return List of topologically sorted features with their required dependencies
     * @throws IllegalArgumentException if a requested Feature is not available
     */
    List<Feature> createSortedFeaturelistWithDependencies(List<String> featureNames) throws IllegalArgumentException;

    /**
     *
     * Checks if all required features is available in the Feature Repository
     * @return List valid features
     */
    List<String> validateFeatureNames(List<String> requestedFeatures);
}
