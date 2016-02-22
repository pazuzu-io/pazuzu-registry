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

    public List<Feature> getAllAvailableFeatures(){
        throw new RuntimeException("not yet implemented");
    }

    public List<Feature> getRequestedFeatures(List<String> featureNames){
        throw new RuntimeException("Not yet implemented");
    }
}
