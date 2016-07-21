package org.zalando.pazuzu.feature.file;

import com.google.common.collect.Iterables;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileContentServiceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void createListRead() throws Exception {
        Path rootPath = folder.getRoot().toPath();
        FileContentService fs = new FileContentService(rootPath); // FIXME Split service into storage-meta
        assertTrue("Self test: Root dir exists.", folder.getRoot().isDirectory());
        assertTrue("Self test: Should be initially empty.", 0 == folder.getRoot().list().length);
        assertTrue("Can list a directory.", fs.searchFiles("*").isEmpty());
        Path filePath = Paths.get("ert.sh");
        String content = "To be or not to be\nNo such question.";
        // Can put a file (no exceptions expected).
        fs.putFile(filePath, new ByteArrayInputStream(content.getBytes()));
        assertEquals("File is indeed in storage location.", 1, folder.getRoot().list().length);
        assertEquals("A stored file is present.", 1, fs.searchFiles("*").size());
        assertEquals("Can list exact match.",
                filePath,
                Iterables.getFirst(fs.searchFiles(filePath.getFileName().toString()), null));
        try (InputStream in = fs.getContentStream(filePath)) {
            assertEquals("Content is preserved.", content, IOUtils.toString(in, Charsets.UTF_8));
        }
    }
}