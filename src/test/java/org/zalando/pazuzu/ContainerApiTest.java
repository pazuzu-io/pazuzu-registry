package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContainerApiTest extends AbstractComponentTest {

    @Test
    public void retrievingContainersShouldReturnEmptyListWhenNoContainersAreStored() {
        ResponseEntity<List> result = template.getForEntity(url("/api/containers"), List.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
