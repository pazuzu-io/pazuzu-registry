package io.pazuzu.registry.model;

public class Feature {

    private FeatureMeta meta;

    private String snippet;

    private String testSnippet;

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getTestSnippet() {
        return testSnippet;
    }

    public void setTestSnippet(String testSnippet) {
        this.testSnippet = testSnippet;
    }

    public FeatureMeta getMeta() {
        return meta;
    }

    public void setMeta(FeatureMeta meta) {
        this.meta = meta;
    }
}
