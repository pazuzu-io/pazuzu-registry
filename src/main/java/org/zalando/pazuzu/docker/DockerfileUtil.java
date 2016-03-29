package org.zalando.pazuzu.docker;

import org.zalando.pazuzu.feature.Feature;
import org.zalando.pazuzu.sort.TopologicalSort;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class DockerfileUtil {

    private DockerfileUtil() {
    }

    public static String generateDockerfile(Optional<String> containerName, Collection<Feature> features) {
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

    private static void collectRecursively(Collection<Feature> result, Feature f) {
        result.add(f);
        f.getDependencies().forEach(item -> collectRecursively(result, item));
    }
}
