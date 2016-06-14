package org.zalando.pazuzu.feature.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vpavlyshyn on 13/06/16.

 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/tags")
public class TagResource {
    private final TagService tagService;

    @Autowired
    public TagResource(TagService tagService) {
        this.tagService = tagService;
    }
//    @RequestMapping(value = "/query/{queryString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public TagDto search(@PathVariable String queryString) throws ServiceException {
//        return tagService.searchTags(queryString, TagDto::ofShort);
//    }
//    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<TagDto> upsert(@RequestBody List<Tag> tags) throws ServiceException {
//        return tagService.upsertTags(tags).stream().map(TagDto::ofShort).collect(Collectors.toList());
//    }

    @RequestMapping( value = "/{tagName}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TagDto createTag(@PathVariable String tagName) throws ServiceException {
        return tagService.upsertTags(Collections.singletonList(new Tag(tagName))).stream().findFirst().map(TagDto::ofShort).orElse(null);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> listTags() {
        return tagService.listTags(TagDto::ofShort);
    }

}
