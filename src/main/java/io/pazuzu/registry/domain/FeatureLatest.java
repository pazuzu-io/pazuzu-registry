package io.pazuzu.registry.domain;

import javax.persistence.*;
import java.util.Objects;
import java.util.Date;


@Entity(name = "FeatureLatest")
@Table(name = "FEATURE_LATEST")
public class FeatureLatest {

    @EmbeddedId
    private FeatureLatestId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("featureId")
    @JoinColumn(name = "FEATURE_ID")
    private Feature feature;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("snippetId")
    @JoinColumn(name = "SNIPPET_ID")
    private Snippet latest;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("baseId")
    @JoinColumn(name = "BASE_ID")
    private FeatureBase base;
    @Column(name="CHANGED_AT")
    private Date changedAt;

    private FeatureLatest() {}

    public FeatureLatest(Feature feature, Snippet snippet, FeatureBase base) {
        this.feature = feature;
        this.latest = snippet;
        this.base = base;
        this.id = new FeatureLatestId(feature.getId(), snippet.getId(), base.getId());
    }

    public FeatureLatestId getId() {
        return id;
    }

    public void setId(FeatureLatestId id) {
        this.id = id;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Snippet getLatest() {
        return latest;
    }

    public void setLatest(Snippet latest) {
        this.latest = latest;
    }

    public FeatureBase getBase() {
        return base;
    }

    public void setBase(FeatureBase base) {
        this.base = base;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureLatest)) return false;
        FeatureLatest that = (FeatureLatest) o;
        return Objects.equals(feature, that.feature) &&
                Objects.equals(latest, that.latest) &&
                Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature, latest, base);
    }
}
