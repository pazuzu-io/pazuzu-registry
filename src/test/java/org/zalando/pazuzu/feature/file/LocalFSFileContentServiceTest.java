package org.zalando.pazuzu.feature.file;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Paths;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LocalFSFileContentServiceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private LocalFSFileContentService mkService() {
        return new LocalFSFileContentService(folder.getRoot().toPath());
    }

    @Test
    public void createFile() throws Exception {
        final LocalFSFileContentService serv = mkService();
        serv.putFile("feature-1", Paths.get("users/file-1.jar"), toInputStream("JAR11", "UTF-8"));
        serv.putFile("feature-1", Paths.get("users/file-2.jar"), toInputStream("JAR12", "UTF-8"));

        serv.putFile("feature-2", Paths.get("users/file-1.jar"), toInputStream("JAR21", "UTF-8"));

        assertEquals("JAR11", IOUtils.toString(serv.getContentStream("feature-1", Paths.get("users/file-1.jar")), "UTF-8"));
        assertEquals("JAR12", IOUtils.toString(serv.getContentStream("feature-1", Paths.get("users/file-2.jar")), "UTF-8"));

        assertEquals("JAR21", IOUtils.toString(serv.getContentStream("feature-2", Paths.get("users/file-1.jar")), "UTF-8"));
    }

    @Test
    public void deleteFile() throws Exception {
        final LocalFSFileContentService serv = mkService();
        serv.putFile("feature-1", Paths.get("users/file-1.jar"), toInputStream("JAR11", "UTF-8"));

        serv.getContentStream("feature-1", Paths.get("users/file-1.jar"));

        serv.delete("feature-1", Paths.get("users/file-1.jar"));

        try {
            serv.getContentStream("feature-1", Paths.get("users/file-1.jar"));
            fail("File should be deleted");
        } catch (IOException e) { }
    }

    @Test
    public void harden() throws Exception {
        final LocalFSFileContentService serv = mkService();
        try {
            serv.putFile("feature-1", Paths.get("../users/xpoit.sh"), toInputStream("rm -rf /", "UTF-8"));
            fail("Relative paths shouldn't be allowed");
        } catch (IllegalArgumentException e) { }
    }
}