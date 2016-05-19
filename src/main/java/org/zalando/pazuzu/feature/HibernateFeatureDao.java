package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class HibernateFeatureDao implements FeatureDao {
    private final EntityManager entityManager;

    @Autowired
    public HibernateFeatureDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Feature> getFeatures(int offset, int limit) {
        return entityManager.createQuery("from Feature f").setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}