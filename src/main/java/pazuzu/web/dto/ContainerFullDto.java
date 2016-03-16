package pazuzu.web.dto;

import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import pazuzu.model.Container;

@XmlRootElement(name = "ContainerFull")
@XmlAccessorType(XmlAccessType.NONE)
public class ContainerFullDto extends ContainerDto {
    @XmlElement(name = "features")
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
