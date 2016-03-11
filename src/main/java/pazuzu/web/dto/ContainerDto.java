package pazuzu.web.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import pazuzu.model.Container;

@XmlRootElement(name = "Container")
@XmlAccessorType(XmlAccessType.NONE)
public class ContainerDto {
    @XmlElement(name = "name")
    private String name;

    public static ContainerDto ofShort(Container container) {
        if (null == container) {
            return null;
        }
        final ContainerDto result = new ContainerDto();
        fillShort(container, result);
        return result;
    }

    protected static void fillShort(Container container, ContainerDto result) {
        result.name = container.getName();
    }
}
