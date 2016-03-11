package pazuzu.web.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import pazuzu.model.Feature;

@XmlRootElement(name = "Feature")
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureDto {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "docker_data")
    private String dockerData;

    public String getName() {
        return name;
    }

    public String getDockerData() {
        return dockerData;
    }

    public static FeatureDto ofShort(Feature feature) {
        if (null == feature) {
            return null;
        }
        final FeatureDto result = new FeatureDto();
        fillShort(feature, result);
        return result;
    }

    protected static void fillShort(Feature feature, FeatureDto result) {
        result.name = feature.getName();
        result.dockerData = feature.getDockerData();
    }

}
