package pazuzu.persistence;

import java.util.Collection;

import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
public interface FeatureRepository {

	Collection<Feature> getFeatures();
}
