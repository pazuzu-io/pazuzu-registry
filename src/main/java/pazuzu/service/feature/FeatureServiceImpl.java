package pazuzu.service.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepository;
import pazuzu.service.FeatureService;
import pazuzu.service.GraphService;

import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    GraphService graphService;

    @Autowired
    FeatureRepository featureRepository;

    @Override
    public List<Feature> getAllFeatures() {
        return null;
    }

    @Override
    public List<Feature> createSortedFeaturelistWithDependencies(List<String> featureNames) throws IllegalArgumentException {
        return null;
    }

    @Override
    public List<String> validateFeatureNames(List<String> requestedFeatures) {
        return null;
    }
}
