package org.zalando.pazuzu.infrastructure.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.PazuzuAppLauncher;
import org.zalando.pazuzu.infrastructure.dto.TagDto;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vpavlyshyn on 15/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public class TagResourceTest {
    private static final List<String> TAG_NAMES = Arrays.asList(new String[]{"node", "npm", "nginx", "java8", "js", "docker"});
    private final TestRestTemplate template = new TestRestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}")
    private int port;
    private URL tagUrl = new URL("http", "localhost", port, "/api/tags");

    public TagResourceTest() throws MalformedURLException {
    }

    @Test
    public void search() throws Exception {
        tagUrl = new URL("http", "localhost", port, "/api/tags");
        Assert.assertTrue(insertTags(TAG_NAMES));
        String queryString = "n";
        ResponseEntity<List> tagsResult = template.getForEntity(tagUrl.toString() + "/query/"
                + queryString, List.class);
        Assert.assertEquals(tagsResult.getBody().size(), TAG_NAMES.stream().filter(t -> t.startsWith(queryString)).count());
    }

    @Test
    public void upsert() throws Exception {
        tagUrl = new URL("http", "localhost", port, "/api/tags");
        Assert.assertTrue(insertTags(TAG_NAMES));
    }

    @Test
    public void listTags() throws Exception {
        tagUrl = new URL("http", "localhost", port, "/api/tags");
        Assert.assertTrue(insertTags(TAG_NAMES));
        List<String> tagNames = Arrays.asList("erlang", "otp", "docker");
        Assert.assertTrue(insertTags(tagNames));
        ResponseEntity<TagDto[]> allTagsEntity = template.getForEntity(tagUrl.toString(), TagDto[].class);
        Set<String> uniqueTags = new HashSet<>(TAG_NAMES);
        uniqueTags.addAll(tagNames);
        Assert.assertEquals(allTagsEntity.getBody().length, uniqueTags.size());


    }

    private boolean insertTags(List<String> tagNames) throws IOException {
        List<TagDto> tags = tagNames.stream().map(TagDto::ofName).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<List<TagDto>> typeRef = new ParameterizedTypeReference<List<TagDto>>() {
        };

        String s = mapper.writeValueAsString(tags);
        HttpEntity<String> tagsEntity = new HttpEntity<String>(s, headers);
        ResponseEntity<String> listResponseEntity = template.exchange(tagUrl.toString(), HttpMethod.POST, tagsEntity, String.class);
        return listResponseEntity.getStatusCode() == HttpStatus.OK;
    }

}