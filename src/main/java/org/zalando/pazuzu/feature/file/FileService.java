package org.zalando.pazuzu.feature.file;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.pazuzu.exception.NotFoundException;
import org.zalando.pazuzu.exception.PlainNotFoundException;
import org.zalando.pazuzu.feature.Feature;
import org.zalando.pazuzu.feature.FeatureRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles file descriptions.
 * <p>
 * TODO (review) Maybe it's worthwhile to store content in DB also. This would simplify deployment/backups and improves data consistency.
 *
 * @see LocalFSFileContentService
 * @see FileResource
 */
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FeatureRepository featureRepository;
    private final FileContentService fileContentService;

    @Inject
    public FileService(FileRepository fileRepository, FeatureRepository featureRepository, FileContentService fileContentService) {
        this.fileRepository = fileRepository;
        this.featureRepository = featureRepository;
        this.fileContentService = fileContentService;
    }

    @Transactional
    public File create(String featureName, String fileName, InputStream content) {
        final Feature feature = featureRepository.findByName(featureName);
        if (feature == null) {
            throw new NotFoundException(String.format("Feature with name '%s' not found", featureName));
        }

        try {
            fileContentService.putFile(featureName, Paths.get(fileName), content);

            final File file = new File(fileName, Paths.get(fileName));
            file.setFeature(feature);

            return fileRepository.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Can't create file.", e);
        }
    }

    public File get(String featureName, int fileId) {
        final File file = fileRepository.findOneByFeatureNameAndId(featureName, fileId);
        if (null == file) {
            throw new NotFoundException(String.format("File #%s is not found.", fileId));
        }
        return file;
    }

    @Transactional
    public void delete(String featureName, int fileId) {
        final File file = get(featureName, fileId);
        fileRepository.delete(file);
        try {
            fileContentService.delete(featureName, Paths.get(file.getContentPath()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot delete content of file #%s.", fileId), e);
        }
    }

    public InputStream getContent(String featureName, int fileId) {
        try {
            return fileContentService.getContentStream(featureName, Paths.get(get(featureName, fileId).getContentPath()));
        } catch (NotFoundException nfe) {
            // XXX Hack. See comments in org.zalando.pazuzu.exception.GlobalExceptionHandler.plainNotFoundException()
            throw new PlainNotFoundException(String.format("No file with #%s.", fileId));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read content of file #%s.", fileId), e);
        }
    }

    public List<File> findByNamePart(String featureName, String nameFragment) {
        return fileRepository.findByFeatureNameAndNameIgnoreCaseContaining(featureName, nameFragment);
    }

    public void approve(String featureName, int fileId) {
        File file = fileRepository.findOneByFeatureNameAndId(featureName, fileId);
        if (file == null) {
            throw new PlainNotFoundException(String.format("Not file with %d.", fileId));
        }
        file.setApproved(true);
        fileRepository.save(file);
    }
}
