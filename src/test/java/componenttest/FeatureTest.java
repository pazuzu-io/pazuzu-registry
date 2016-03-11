package componenttest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;

public class FeatureTest extends AbstractComponentTest {

    // Just an example
    @Test
    public void featureref_returns_some_features() throws Exception {

        ResponseEntity<List> result = template.getForEntity("http://localhost:8080/featurerefs", List.class);

        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
