package componenttest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ContainerTest extends AbstractComponentTest {
    @Test
    public void testEmptyContainersList() {
        ResponseEntity<List> result = template.getForEntity(url("/api/containers"), List.class);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
//        Assert.assertEquals(0, result.getBody().size());
    }
}
