package org.zalando.pazuzu.feature.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serving list of file available for addition to images.
 * <p>
 * FIXME File paths are unsuitable generally as  "/" and filename extensions (suffixes) interfere with spring's request mapping.
 */
@RestController
@RequestMapping(value = "/api/files")
public class FileResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);

    private final FileService fileService;

    @Autowired
    public FileResource(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FileDto> searchFiles(@RequestParam(required = false, name = "q", defaultValue = "") String queryString) {
        return fileService.findByNamePart(queryString)
                .stream().map(FileDto::fromFile)
                .collect(Collectors.toList());
    }

    /**
     * The API might be unclear here. Putting name into query param meant to simplify using general purpose
     * tools like curl. Could be more consistent to pass all fields as multipart though.
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDto> createFile(@RequestParam(required = false, name = "name") String fileName,
                                              InputStream inputStream, UriComponentsBuilder uriBuilder) {
        final FileDto fd = FileDto.fromFile(fileService.create(fileName, inputStream));
        return ResponseEntity
                .created(uriBuilder.path("/api/files/{fileId}").buildAndExpand(fd.getId()).toUri())
                .body(fd);
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FileDto getFile(@PathVariable Integer fileId) {
        return FileDto.fromFile(fileService.get(fileId));
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteFile(@PathVariable Integer fileId) {
        fileService.delete(fileId);
    }

    @RequestMapping(value = "/{fileId}/content", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFileContent(@PathVariable String fileId, OutputStream responseStream) {
        try {
            IOUtils.copy(fileService.getContent(Integer.parseInt(fileId)), responseStream);
        } catch (IOException e) {
            LOG.error("File search error", e);
            throw new RuntimeException("Error getting file content.", e);
        }
    }
}
