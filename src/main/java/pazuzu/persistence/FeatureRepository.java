package pazuzu.persistence;

import pazuzu.model.Feature;

import java.util.List;

/**
 * Created by cseidel on 22/02/16.
 */
public interface FeatureRepository {

    List<Feature> getFeatures();
}
