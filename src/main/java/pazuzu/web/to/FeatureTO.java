package pazuzu.web.to;

import pazuzu.model.Feature;

import java.util.function.Function;

/**
 * Created by cseidel on 22/02/16.
 *
 * Feature Transfer Object
 */
public class FeatureTO {
    public final String name;
    public final String docker_snippet;

    public FeatureTO(String name, String docker_snippet) {
        this.name = name;
        this.docker_snippet = docker_snippet;
    }

    public static final Function<Feature, FeatureTO> byFeature = feature -> new FeatureTO(feature.name, feature.dockerfile_snippet);
}
