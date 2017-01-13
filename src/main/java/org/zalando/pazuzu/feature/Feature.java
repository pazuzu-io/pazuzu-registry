package org.zalando.pazuzu.feature;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Feature {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "feature_dependency",
            joinColumns = @JoinColumn(name = "feature_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "dependency_feature_id", nullable = false))
    private Set<Feature> dependencies;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "author", length = 256)
    private String author;
    @Column(name = "snippet")
    private String snippet;
    @Column(name = "test_snippet")
    private String testSnippet;

    public Set<Feature> getDependencies() {
        return dependencies;
    }

    public Feature setDependencies(Set<Feature> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Feature setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Feature setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Feature setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }


    public String getAuthor() {
        return author;
    }

    public Feature setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getSnippet() {
        return snippet;
    }

    public Feature setSnippet(String snippet) {
        this.snippet = snippet;
        return this;
    }

    public String getTestSnippet() {
        return testSnippet;
    }

    public Feature setTestSnippet(String testSnippet) {
        this.testSnippet = testSnippet;
        return this;
    }

    @PrePersist
    private void setDatesAtCreation() {
        this.createdAt = new Date();
        setUpdatedAt();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = new Date();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Feature)) {
            return false;
        }

        Feature other = (Feature) obj;
        return this.getId() == other.getId(); // TODO (review) Is comparison with "==" intended?
    }

    public boolean containsDependencyRecursively(Feature existing) {
        if (this == existing) {
            return true;
        }
        return dependencies.stream().anyMatch(item -> item.containsDependencyRecursively(existing));
    }

}
