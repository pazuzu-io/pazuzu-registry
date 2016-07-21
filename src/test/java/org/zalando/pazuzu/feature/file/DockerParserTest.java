package org.zalando.pazuzu.feature.file;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

public class DockerParserTest {

    @Test
    public void testParser() throws Exception {
        Assert.assertEquals(Sets.newHashSet(), DockerParser.getCopyFiles("RUN ls"));
        Assert.assertEquals(Sets.newHashSet("asd-asd.txt"), DockerParser.getCopyFiles("ADD asd-asd.txt /opt/config.yaml"));
        Assert.assertEquals(
                Sets.newHashSet("asd-asd.txt", "gorg.hex"),
                DockerParser.getCopyFiles("ADD asd-asd.txt /opt/config.yaml\n" +
                        "COPY gorg.hex /opt/gorg.hex\n" +
                        "    \t ADD     asd-asd.txt   /etc/backdoor.sh"));
    }
}