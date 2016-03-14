package pazuzu.web.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FeatureToCreate")
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureToCreateDto extends FeatureDto {
    @XmlElement(name = "dependencies")
    private List<String> dependencies;

    public List<String> getDependencies() {
        if (null == dependencies) {
            dependencies = new ArrayList<>();
        }
        return dependencies;
    }
}
