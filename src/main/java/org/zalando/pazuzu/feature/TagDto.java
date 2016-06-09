package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpavlyshyn on 09/06/16.
 */
public class TagDto {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("name")
    private String name;

    public static TagDto ofShort(Tag feature) {
        if (null == feature) {
            return null;
        }
        final TagDto result = new TagDto();
        fillShort(feature, result);
        return result;
    }

    private static void fillShort(Tag featureTag, TagDto result) {
        result.setName(featureTag.getName());
    }
}
