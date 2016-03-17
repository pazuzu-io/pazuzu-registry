package pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pazuzu.model.Container;

public class ContainerDto {
    @JsonProperty("name")
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
