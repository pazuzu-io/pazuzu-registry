package pazuzu.web.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ContainerToCreateDto {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "features")
    private List<String> features;

    public String getName() {
        return name;
    }

    public List<String> getFeatures() {
        return features;
    }
}
