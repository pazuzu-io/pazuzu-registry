package io.pazuzu.registry.model;

public class FeatureListLinks {
    private Link prev;

    private Link next;

    public Link getPrev() {
        return prev;
    }

    public void setPrev(Link prev) {
        this.prev = prev;
    }

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    public FeatureListLinks() {
    }

    public FeatureListLinks(Link prev, Link next) {
        this.prev = prev;
        this.next = next;
    }
}
