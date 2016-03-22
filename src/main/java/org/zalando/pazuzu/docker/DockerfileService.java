package org.zalando.pazuzu.docker;

import org.springframework.stereotype.Service;
import org.zalando.pazuzu.container.Container;
import org.zalando.pazuzu.feature.Feature;
import org.zalando.pazuzu.sort.TopologicalSort;

import java.util.*;

@Service
public class DockerfileService {

    public String generateDockerfile(Container container) {
        return generateDockerfile(Optional.of(container.getName()), container.getFeatures());
    }

    public String generateDockerfile(Collection<Feature> features) {
        return generateDockerfile(Optional.empty(), features);
    }

    private String generateDockerfile(Optional<String> containerName, Collection<Feature> features) {
        final Set<Feature> expandedList = new HashSet<>();
        features.forEach(f -> collectRecursively(expandedList, f));

        List<Feature> orderedFeatures = TopologicalSort.sort(expandedList, (a, b) -> a.getDependencies().contains(b));

        final StringBuilder dockerFileString = new StringBuilder("# Auto-generated DockerFile by Pazuzu2\n");
        dockerFileString.append(containerName.isPresent() ? ("# Generated from container " + containerName + "\n\n") : "\n");
        dockerFileString.append("FROM ubuntu:latest\n\n");
        orderedFeatures.stream().forEachOrdered(feature -> {
            dockerFileString.append("# ").append(feature.getName()).append("\n");
            dockerFileString.append(feature.getDockerData()).append("\n\n");
        });
        dockerFileString.append("CMD /bin/bash\n");
        return dockerFileString.toString();
    }

    private void collectRecursively(Collection<Feature> result, Feature f) {
        result.add(f);
        f.getDependencies().forEach(item -> collectRecursively(result, item));
    }
}
