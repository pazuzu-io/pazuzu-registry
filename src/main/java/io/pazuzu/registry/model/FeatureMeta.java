package io.pazuzu.registry.model;

import java.util.Date;
import java.util.List;

public class FeatureMeta {
    private String name;

    private String description;

    private String author;

    private FeatureStatus status;

    private Date updatedAt;

    private Date createdAt;

    private List<String> dependencies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public FeatureStatus getStatus() {
        return status;
    }

    public void setStatus(FeatureStatus status) {
        this.status = status;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public FeatureMeta() {
    }

    public FeatureMeta(String name, String description, String author, FeatureStatus status, Date updatedAt, Date createdAt, List<String> dependencies) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.status = status;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.dependencies = dependencies;
    }

    public enum FeatureStatus {

        approved("approved"),
        declined("declined"),
        pending("pending");

        private final String jsonValue;

        FeatureStatus(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public String jsonValue() {
            return jsonValue;
        }


    }
}
