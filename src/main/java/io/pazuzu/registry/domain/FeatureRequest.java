package io.pazuzu.registry.domain;

import javax.persistence.*;

@Entity(name = "FeatureRequest")
@Table(name = "FEATURE_REQUEST")
public class FeatureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
}
