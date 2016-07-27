package org.zalando.pazuzu.feature.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by pzalunin on 27/07/16.
 */
public interface FileContentService {
    void putFile(String featureName, Path path, InputStream content) throws IOException;

    InputStream getContentStream(String featureName, Path fileName) throws IOException;

    void delete(String featureName, Path fileName) throws IOException;
}
