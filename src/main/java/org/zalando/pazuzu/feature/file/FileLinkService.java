package org.zalando.pazuzu.feature.file;

import org.springframework.stereotype.Service;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
import org.zalando.pazuzu.exception.FileNotFoundException;
import org.zalando.pazuzu.feature.Feature;
import org.zalando.pazuzu.feature.FeatureRepository;

import javax.inject.Inject;
import java.util.Collection;

@Service
public class FileLinkService {
    private final FeatureRepository featureRepository;
    private final FileRepository fileRepository;

    @Inject
    public FileLinkService(FeatureRepository featureRepository, FileRepository fileRepository) {
        this.featureRepository = featureRepository;
        this.fileRepository = fileRepository;
    }

    public Collection<File> getFeatureFiles(String featureName) {
        return getFeature(featureName).getFiles();
    }

    public void link(String featureName, Integer fileId) {
        final File file = fileRepository.findOne(fileId);
        if (null == file) {
            throw new FileNotFoundException("File #" + fileId + " is not found.");
        }
        final Feature feature = getFeature(featureName);
        feature.getFiles().add(file);
        featureRepository.save(feature);
    }

    public void unlink(String featureName, Integer fileId) {
        final Feature feature = getFeature(featureName);
        feature.getFiles().removeIf(f -> f.getId().equals(fileId));
        featureRepository.save(feature);
    }

    private Feature getFeature(String featureName) {
        final Feature byName = featureRepository.findByName(featureName);
        if (null == byName) {
            throw new FeatureNotFoundException("Feature '" + featureName + "' is not found.");
        }
        return byName;
    }

}
