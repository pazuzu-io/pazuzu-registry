package org.zalando.pazuzu.feature.file;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by pzalunin on 27/07/16.
 */
@Service
@Profile("test")
public class InMemoryFileContentService implements FileContentService {

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<String, byte[]> files = new HashMap<>();

    private String mkKey(String featureName, Path path) {
        return featureName + "/" + path.toString();
    }

    @Override
    public void putFile(String featureName, Path path, InputStream content) throws IOException {
        lock.lock();
        try {
            files.put(mkKey(featureName, path), IOUtils.toByteArray(content));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public InputStream getContentStream(String featureName, Path fileName) throws IOException {
        lock.lock();
        try {
            final String key = mkKey(featureName, fileName);
            final byte[] cntnt = files.get(key);
            if (cntnt == null) {
                throw new IOException(String.format("File '%s' not found.", key));
            }
            return new ByteArrayInputStream(cntnt);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String featureName, Path fileName) throws IOException {
        lock.lock();
        try {
            final String key = mkKey(featureName, fileName);
            if (files.remove(key) == null) {
                throw new IOException(String.format("File '%s' not found.", key));
            }
        } finally {
            lock.unlock();
        }
    }

    public void clean() {
        lock.lock();
        try {
            files.clear();
        } finally {
            lock.unlock();
        }
    }
}
