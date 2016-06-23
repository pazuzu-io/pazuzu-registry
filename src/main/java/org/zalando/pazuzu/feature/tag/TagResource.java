package org.zalando.pazuzu.feature.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
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

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/query/{queryString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> search(@PathVariable String queryString) throws ServiceException {
        return tagService.searchTags(queryString).stream().map(TagDto::ofShort).collect(Collectors.toList());
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<org.zalando.pazuzu.feature.tag.TagDto> upsert(@RequestBody List<TagDto> tags) throws ServiceException {
        return tagService.upsertTagDtos(tags).stream().map(TagDto::ofShort).collect(Collectors.toList());
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> listTags() {
        return tagService.listTags().stream().map(TagDto::ofShort).collect(Collectors.toList());
    }
}
