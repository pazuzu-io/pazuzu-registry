package pazuzu.web.services.to;

import pazuzu.web.services.Dao.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
public class FeatureRefTO {
    public final String name;
    public final String description;

    public FeatureRefTO(String name, String description){
        this.name = name;
        this.description = description;
    }

    /**
     * create FeatureRefTO by Feature Object
     * @param feature Referenced Feature
     * @return TransferObject FeatureRefTO
     */
    public static FeatureRefTO byFeature(Feature feature){
        return new FeatureRefTO(feature.name, feature.description);
    }
}
