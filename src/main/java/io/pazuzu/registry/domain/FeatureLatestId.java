package io.pazuzu.registry.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FeatureLatestId implements Serializable {

    @Column(name="FEATURE_ID")
    private Long featureId;
    @Column(name="SNIPPET_ID")
    private Long snippetId;
    @Column(name="BASE_ID")
    private Long baseId;

    private FeatureLatestId() {}

    public FeatureLatestId(Long featureId, Long snippetId, Long baseId) {
        this.featureId = featureId;
        this.snippetId = snippetId;
        this.baseId = baseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureLatestId)) return false;
        FeatureLatestId that = (FeatureLatestId) o;
        return Objects.equals(featureId, that.featureId) &&
                Objects.equals(snippetId, that.snippetId) &&
                Objects.equals(baseId, that.baseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureId, snippetId, baseId);
    }
}
