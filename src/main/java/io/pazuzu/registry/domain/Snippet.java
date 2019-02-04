package io.pazuzu.registry.domain;

import javax.persistence.*;
import java.util.*;

@Entity(name = "Snippet")
@Table(name = "SNIPPET")
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "SNIPPET")
    private String snippet;
    @Column(name = "ACTIVE", nullable = false)
    private boolean active;
    @OneToMany(
            mappedBy = "base",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FeatureLatest> latestBase = new HashSet<>();
    @OneToMany(
            mappedBy = "feature",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FeatureLatest> featureLatest = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "SNIPPET_DEPENDENCY",
            joinColumns = @JoinColumn(name = "DEPENDENCY_ID", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "SNIPPET_ID", nullable = false))
    private Set<Snippet> dependencies = new HashSet<Snippet>();

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "PACKAGE_DEPENDENCY",
            joinColumns = @JoinColumn(name = "DEPENDENCY_ID"),
            inverseJoinColumns = @JoinColumn(name = "SNIPPET_ID"))
    private Set<Package> packages = new HashSet<Package>();


    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name = "RESOURCE_DEPENDENCY",
            joinColumns = @JoinColumn(name = "DEPENDENCY_ID"),
            inverseJoinColumns = @JoinColumn(name = "SNIPPET_ID"))
    private Set<Resource> resources = new HashSet<Resource>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CREATED_BY", nullable = false)
    private User creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Snippet> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Snippet> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<Package> getPackages() {
        return packages;
    }

    public void setPackages(Set<Package> packages) {
        this.packages = packages;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<FeatureLatest> getLatestBase() {
        return latestBase;
    }

    public void setLatestBase(Set<FeatureLatest> latestBase) {
        this.latestBase = latestBase;
    }

    public Set<FeatureLatest> getFeatureLatest() {
        return featureLatest;
    }

    public void setFeatureLatest(Set<FeatureLatest> featureLatest) {
        this.featureLatest = featureLatest;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    @PrePersist
    public void setDatesAtCreation() {
        this.createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snippet)) return false;
        Snippet snippet = (Snippet) o;
        return Objects.equals(id, snippet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
