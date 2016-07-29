package org.zalando.pazuzu.feature.file;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Simplest implementation using filesystem for storage.
 * Does not provide referential integrity as file can be removed from filesystem even if referenced from a feature.
 * When used inside Docker container this would require volume mapping for storageRoot.
 * <p>
 * To be added to image the file should be referred with "COPY" command in Dockerfile.
 * TODO A Dockerfile preprocessor should then replace filenames with paths relative to storage root with references to REST service.
 * TODO Support for "ADD" command needs to be implemented in CLI: Necessary file should be copied to CLI's machine before build.
 * <p>
 * Further improvements:
 * TODO use a DB table containing either checksums and source file paths or file contents in BLOBS.
 * Latter simplifies deployment and is likely sufficient for all usecases (no big blobs, little number of records)
 */
@Service
@Profile({"production", "dev"})
public class LocalFSFileContentService implements FileContentService {

    private final Path storageRoot;

    @Autowired
    public LocalFSFileContentService(@Named("fileStorageRoot") Path storageRoot) {
        this.storageRoot = storageRoot;
    }

    @Override
    public void putFile(String featureName, Path path, InputStream content) throws IOException {
        Files.copy(content, getRealPath(featureName, path, true), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public InputStream getContentStream(String featureName, Path fileName) throws IOException {
        return Files.newInputStream(getRealPath(featureName, fileName, false), StandardOpenOption.READ);
    }

    @Override
    public void delete(String featureName, Path fileName) throws IOException {
        Files.delete(getRealPath(featureName, fileName, false));
    }

    private Path getRealPath(String featureName, Path filePath, boolean createSubdirs) throws IOException {
        Preconditions.checkNotNull(featureName, "featureName should be defined.");
        Preconditions.checkNotNull(filePath, "filePath should be defined.");
        Preconditions.checkArgument(filePath.getNameCount() > 0, "filePath should contain at least one segment");

        final Path storagePath = storageRoot.resolve(featureName).resolve(filePath);

        if (!storagePath.normalize().startsWith(storageRoot.resolve(featureName))) {
            throw new IllegalArgumentException("File path should be absolute.");
        }

        if (createSubdirs) {
            Files.createDirectories(storagePath.getParent());
        }

        return storagePath;
    }
}
