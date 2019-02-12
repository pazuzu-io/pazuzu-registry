package io.pazuzu.registry.domain;

import javax.persistence.*;

@Entity(name = "FeatureRequestStatus")
@Table(name = "FEATURE_REQUEST_STATUS")
public class FeatureRequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
}
