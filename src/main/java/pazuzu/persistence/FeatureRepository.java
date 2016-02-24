package pazuzu.persistence;

import java.util.Collection;
import java.util.List;

import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
public interface FeatureRepository {

	List<Feature> getFeatures();
}
