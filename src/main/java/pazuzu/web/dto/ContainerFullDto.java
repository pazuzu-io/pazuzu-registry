package pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import pazuzu.model.Container;

public class ContainerFullDto extends ContainerDto {
    @JsonProperty("features")
    private List<FeatureDto> features;

    public List<FeatureDto> getFeatures() {
        return features;
    }

    public static ContainerFullDto buildFull(Container container) {
        if (null == container) {
            return null;
        }
        final ContainerFullDto result = new ContainerFullDto();
        fillShort(container, result);
        result.features = container.getFeatures().stream().map(FeatureDto::ofShort).collect(Collectors.toList());
        return result;
    }
}
