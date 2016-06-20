package org.zalando.pazuzu.infrastructure.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.PazuzuAppLauncher;
import org.zalando.pazuzu.infrastructure.domain.Tag;
import org.zalando.pazuzu.infrastructure.dto.TagDto;
import org.zalando.pazuzu.infrastructure.service.TagService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by vpavlyshyn on 13/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")

public class TagServiceTest {
    private static final List<String> TAG_NAMES = Arrays.asList("test", "test", "tag", "java", "js");
    @Autowired
    private TagService tagService;

    @Before
    public void initialize() {
        List<Tag> tags = tagService.upsertTagDtos(TAG_NAMES.stream().map(TagDto::ofName).collect(Collectors.toList()));
    }

    @Test
    public void upsertUniqueTags() throws Exception {
        List<String> newTagNames = Arrays.asList("test", "java", "npm");
        List<Tag> resultTags = tagService.upsertTagDtos(newTagNames.stream().map(TagDto::ofName).collect(Collectors.toList()));
        Assert.assertEquals(resultTags.size(), newTagNames.size());

        for (Tag tag : resultTags) {
            Assert.assertTrue(newTagNames.contains(tag.getName()));
        }
    }

    @Test
    public void listTags() throws Exception {
        List<Tag> queryResult = tagService.listTags();
        Set<String> expectedTags = new HashSet<>(TAG_NAMES);
        Assert.assertEquals(tagService.listTags().size(), expectedTags.size());

        for (Tag tag : queryResult) {
            Assert.assertTrue(TAG_NAMES.contains(tag.getName()));
        }
    }

    @Test
    public void searchTags() throws Exception {
        String query = "t";
        List<Tag> resultTags = tagService.searchTags(query);
        for (Tag tag : resultTags) {
            Assert.assertTrue(tag.getName().startsWith(query));
        }

    }

    @Test
    public void searchTagsNoResults() {
        String query = "we";
        List<Tag> resultTags = tagService.searchTags(query);
        Assert.assertTrue(resultTags.isEmpty());
    }

    @Test
    public void searchTagsEmptyQuery() {
        List<Tag> resultTags = tagService.searchTags("");
        Assert.assertTrue(resultTags.isEmpty());
    }

    @Test
    public void searchTagsNullQuery() {
        List<Tag> resultTags = tagService.searchTags(null);
        Assert.assertTrue(resultTags.isEmpty());
    }

}