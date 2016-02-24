package pazuzu.service.feature;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepository;
import pazuzu.service.FeatureService;
import pazuzu.service.graph.Graph;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    public FeatureRepository featureRepository;

    @Override
    public Collection<Feature> getAllFeatures() {
        //query the feature repository to get all the features
        return featureRepository.getFeatures();
    }

    @Override
    public List<Feature> createSortedFeaturelistWithDependencies(List<String> featureNames) throws IllegalArgumentException {
        Graph graph = new Graph();
        graph.buildGraph(featureRepository.getFeatures());
        List<String> sortedFeatureNamesList =  graph.Tsort(featureNames);
        return featureRepository.getFeatures().stream()
                .filter(feature -> sortedFeatureNamesList.contains(feature.name))
                .collect(Collectors.toList());

    }

    @Override
    public List<String> validateFeatureNames(List<String> requestedFeatures) {
        return featureRepository.getFeatures().stream()
                .map(feature -> feature.name)
                .filter(s -> requestedFeatures.contains(s))
                .collect(Collectors.toList());
    }
}
