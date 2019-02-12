package io.pazuzu.registry.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.*;


@Entity(name = "Feature")
@Table(name = "FEATURE")
@NaturalIdCache
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NaturalId
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "ACTIVE", nullable = false)
    private boolean active;
    @Column(name = "VERSION")
    private String version;
    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="AUTHOR_ID")
    private User author;
    @ManyToMany(mappedBy = "features")
    private Set<User> owner = new HashSet<>();

    @OneToMany(
            mappedBy = "latest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FeatureLatest> latest = new HashSet<>();
    @OneToMany(
            mappedBy = "base",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FeatureLatest> latestBase = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    public Feature() {
    }

    public Feature(String name) {
        this.name = name;
    }


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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<User> getOwner() {
        return owner;
    }

    public Set<FeatureLatest> getLatest() {
        return latest;
    }

    public void setLatest(Set<FeatureLatest> latest) {
        this.latest = latest;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setOwner(Set<User> owner) {
        this.owner = owner;
    }

    public Set<FeatureLatest> getLatestBase() {
        return latestBase;
    }

    public void setLatestBase(Set<FeatureLatest> latestBase) {
        this.latestBase = latestBase;
    }

    @PrePersist
    public void setDatesAtCreation() {
        this.createdAt = new Date();
        setUpdatedAt();
    }

    @PreUpdate
    public void setUpdatedAt() {

        this.updatedAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature)) return false;
        Feature feature = (Feature) o;
        return Objects.equals(id, feature.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
