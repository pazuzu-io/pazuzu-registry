package componenttest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class FeatureTest extends AbstractComponentTest {

    // Just an example
    @Test
    public void ensureThatGetOnTheFeaturerefResourceReturnsStatusCode200() throws Exception {

        ResponseEntity<List> result = template.getForEntity("http://localhost:8080/api/features", List.class);

        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
