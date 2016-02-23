package pazuzu.web.to;

import pazuzu.model.Feature;

import java.util.function.Function;

/**
 * Created by cseidel on 22/02/16.
 *
 * Feature Reference Transfere Object
 */
public class FeatureRefTO {
    public final String name;
    public final String description;

    /**
     * Constructor
     * Create FeatureReference TransferObject by stating the name and description explicitly
     * @param name name of the feature
     * @param description a short description for the feature
     */
    public FeatureRefTO(String name, String description){
        this.name = name;
        this.description = description;
    }

    public static final Function<Feature, FeatureRefTO> byFeature = feature -> new FeatureRefTO(feature.name, feature.description);
}
