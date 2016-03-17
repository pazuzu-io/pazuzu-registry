package componenttest;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pazuzu.web.dto.FeatureFullDto;
import pazuzu.web.dto.FeatureToCreateDto;

public class FeatureTest extends AbstractComponentTest {

    @Test
    public void testEmptyList() throws Exception {
        ResponseEntity<List> result = template.getForEntity(url("/api/features"), List.class);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assert.assertEquals(0, result.getBody().size());
    }

    @Test
    public void testFeatureCreate() throws Exception {
        final FeatureToCreateDto dto = new FeatureToCreateDto();
        dto.setName("Test");
        dto.setDockerData("Test Data");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<FeatureToCreateDto> entity = new HttpEntity<>(dto, headers);
        final ResponseEntity<FeatureFullDto> result = template.postForEntity(url("/api/features"), entity, FeatureFullDto.class);
        Assert.assertEquals(200, result.getStatusCode().value());
        final FeatureFullDto resultFeature = result.getBody();
        Assert.assertEquals(dto.getName(), resultFeature.getName());
        Assert.assertEquals(dto.getDockerData(), resultFeature.getDockerData());
        Assert.assertEquals(0, dto.getDependencies().size());
    }
}
