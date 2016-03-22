package org.zalando.pazuzu;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractComponentTest {

    @Value("${local.server.port}")
    private int port;

    protected final TestRestTemplate template = new TestRestTemplate();

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }
}
