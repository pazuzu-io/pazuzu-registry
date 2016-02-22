package pazuzu.web.to;

/**
 * Created by cseidel on 22/02/16.
 */
public class FeatureTO {
    public final String name;
    public final String docker_snippet;

    public FeatureTO(String name, String docker_snippet) {
        this.name = name;
        this.docker_snippet = docker_snippet;
    }
}
