package pazuzu.web.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FeatureToAdd")
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureToAddDto {
    @XmlElement(name = "name")
    private String name;

    public String getName() {
        return name;
    }
}
