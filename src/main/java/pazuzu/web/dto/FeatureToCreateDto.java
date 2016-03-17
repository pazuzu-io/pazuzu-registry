package pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class FeatureToCreateDto extends FeatureDto {
    @JsonProperty("dependencies")
    private List<String> dependencies;

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getDependencies() {
        if (null == dependencies) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }
}
