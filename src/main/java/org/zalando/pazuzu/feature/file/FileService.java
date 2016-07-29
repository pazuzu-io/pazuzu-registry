package org.zalando.pazuzu.feature.file;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.pazuzu.exception.FileNotFoundException;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles file descriptions.
 * <p>
 * TODO (review) Maybe it's worthwhile to store content in DB also. This would simplify deployment/backups and improves data consistency.
 *
 * @see FileContentService
 * @see FileResource
 */
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileContentService fileContentService;

    @Inject
    public FileService(FileRepository fileRepository, FileContentService fileContentService) {
        assert null != fileRepository : "FileRepository is required";
        this.fileRepository = fileRepository;
        assert null != fileContentService : "FileContentService is required";
        this.fileContentService = fileContentService;
    }

    @Transactional
    public File create(String fileName, InputStream content) {
        if (null != fileRepository.findByName(fileName)) {
            // XXX Probably it's better to produce error with "conflict" status code here.
            throw new IllegalStateException(String.format("File with name '%s' already exists.", fileName));
        }
        try {
            final Path path = Paths.get(fileName);
            fileContentService.putFile(path, content);
            return fileRepository.save(new File(fileName, path));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create file.", e);
        }
    }

    public File get(int fileId) {
        final File file = fileRepository.findOne(fileId);
        if (null == file) {
            throw new FileNotFoundException(String.format("File #%s is not found.", fileId));
        }
        return file;
    }

    public File getByName(String name) {
        final File file = fileRepository.findByName(name);
        if (null == file) {
            throw new FileNotFoundException(String.format("File with name '%s' is not found.", name));
        }
        return file;
    }

    @Transactional
    public void delete(int fileId) {
        final File file = get(fileId);
        fileRepository.delete(file);
        try {
            fileContentService.delete(Paths.get(file.getContentPath()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot delete content of file #%s.", fileId), e);
        }
    }

    public InputStream getContent(int fileId) {
        try {
            return fileContentService.getContentStream(Paths.get(get(fileId).getContentPath()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read content of file #%s.", fileId), e);
        }
    }

    public List<File> findByNamePart(String nameFragment) {
        return fileRepository.findByNameIgnoreCaseContaining(nameFragment);
    }
}
