package componenttest;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pazuzu.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@IntegrationTest("server.port:0")
@WebIntegrationTest
public class AbstractComponentTest {
    @Value("${local.server.port}")
    private int port;

    protected final TestRestTemplate template = new TestRestTemplate();

    public AbstractComponentTest() {
    }

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }
}
