package org.zalando.pazuzu.feature.file;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("content_href")
    private String contentHref;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentHref() {
        return contentHref;
    }

    public void setContentHref(String contentHref) {
        this.contentHref = contentHref;
    }

    public static FileDto fromFile(File f) {
        final FileDto fd = new FileDto();
        fd.setId(f.getId());
        fd.setName(f.getName());
        fd.setContentHref(String.format("/api/files/%s/content", f.getId())); // XXX Path is hardcoded
        return fd;
    }
}
