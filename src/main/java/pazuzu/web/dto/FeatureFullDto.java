package pazuzu.web.dto;

import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import pazuzu.model.Feature;

@XmlRootElement(name = "FeatureFull")
public class FeatureFullDto extends FeatureDto {
    @XmlElement(name = "dependencies")
    private List<FeatureDto> dependencies;

    public static FeatureFullDto makeFull(Feature feature) {
        if (null == feature) {
            return null;
        }
        final FeatureFullDto result = new FeatureFullDto();
        fillShort(feature, result);
        result.dependencies = feature.getDependencies().stream().map(FeatureDto::ofShort).collect(Collectors.toList());
        return result;
    }
}
