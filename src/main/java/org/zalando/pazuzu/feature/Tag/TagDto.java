package org.zalando.pazuzu.feature.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpavlyshyn on 09/06/16.
 */
public class TagDto {
    @JsonProperty("name")
    private String name;

    @JsonIgnore
    public static TagDto ofShort(Tag tag) {
        if (null == tag) {
            return null;
        }
        final TagDto result = new TagDto();
        fillShort(tag, result);
        return result;
    }

    public static TagDto ofName(String name) {
        TagDto result = new TagDto();
        result.setName(name);
        return result;
    }

    private static void fillShort(Tag featureTag, TagDto result) {
        result.setName(featureTag.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
