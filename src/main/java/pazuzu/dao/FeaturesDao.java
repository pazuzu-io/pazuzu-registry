package pazuzu.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pazuzu.model.Feature;

@Repository
public class FeaturesDao {
    @PersistenceContext
    private EntityManager em;

    public List<Feature> listFeatures(String name) {
        if (!StringUtils.isEmpty(name)) {
            return em.createQuery("select f from Feature f order by f.name", Feature.class).getResultList();
        } else {
            return em.createQuery("select f from Feature f where f.name like :name order by f.name", Feature.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        }
    }

    public Feature findByName(String name) {
        final List<Feature> features = em.createQuery("select f from Feature f where f.name=:name", Feature.class)
                .setParameter("name", name)
                .getResultList();
        if (features.isEmpty()) {
            return null;
        }
        return features.get(0);

    }

    public void save(Feature newFeature) {
        em.persist(newFeature);
    }

    public List<Feature> findReferencingFeatures(Feature feature) {
        return em.createQuery("select f from Feature f where f.dependencies contains :feature", Feature.class)
                .setParameter("feature", feature)
                .getResultList();
    }

    public void deleteFeature(Feature feature) {
        em.remove(feature);
    }
}
