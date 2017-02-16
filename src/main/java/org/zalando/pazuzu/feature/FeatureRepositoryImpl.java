package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class FeatureRepositoryImpl implements FeatureRepositoryCustom {

    @Autowired
    private EntityManager em;

    @Override
    public List<Feature> getFeatures(int offset, int limit) {
        return em.createQuery("from Feature f", Feature.class).setFirstResult(offset).setMaxResults(limit).getResultList();
    }

}
