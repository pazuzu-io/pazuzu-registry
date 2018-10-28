package io.pazuzu.registry.model;

public class Link {

    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Link() {
    }

    public Link(String href) {
        this.href = href;
    }
}
