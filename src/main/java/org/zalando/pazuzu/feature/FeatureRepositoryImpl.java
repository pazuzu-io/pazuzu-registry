package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class FeatureRepositoryImpl implements FeatureRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Feature> getFeatures(int offset, int limit) {
        return entityManager.createQuery("from Feature f").setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
