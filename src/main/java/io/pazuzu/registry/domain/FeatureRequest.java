package io.pazuzu.registry.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "FeatureRequest")
@Table(name = "FEATURE_REQUEST")
public class FeatureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureRequest)) return false;
        FeatureRequest that = (FeatureRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
