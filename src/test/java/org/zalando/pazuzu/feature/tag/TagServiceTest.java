package org.zalando.pazuzu.feature.tag;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.PazuzuAppLauncher;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by vpavlyshyn on 13/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
public class TagServiceTest {
    @Autowired
    private TagService tagService;
    @Test
    public void upsertTags() throws Exception {
        List<Tag> tags = tagService.upsertTags(Collections.singletonList(new Tag("boo")));
        Assert.assertEquals(tags.size(), 1);

    }

    @Test
    public void listTags() throws Exception {

    }

    @Test
    public void searchTags() throws Exception {

    }

}