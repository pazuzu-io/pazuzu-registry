package org.zalando.pazuzu.container;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerDto {

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

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
