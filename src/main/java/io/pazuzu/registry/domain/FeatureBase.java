package io.pazuzu.registry.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "FeatureBase")
@Table(name = "FEATURE_BASE")
@NaturalIdCache
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public class FeatureBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NaturalId
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(
            mappedBy = "latest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<FeatureLatest> latest = new HashSet<>();
    @OneToMany(
            mappedBy = "feature",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private Set<FeatureLatest> featureLatest = new HashSet<>();

    public FeatureBase() {

    }

    public FeatureBase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<FeatureLatest> getLatest() {
        return latest;
    }

    public void setLatest(Set<FeatureLatest> latest) {
        this.latest = latest;
    }

    public Set<FeatureLatest> getFeatureLatest() {
        return featureLatest;
    }

    public void setFeatureLatest(Set<FeatureLatest> featureLatest) {
        this.featureLatest = featureLatest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureBase)) return false;
        FeatureBase that = (FeatureBase) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
