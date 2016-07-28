package org.zalando.pazuzu.feature.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by vpavlyshyn on 14/06/16.
 */
@Service
public class TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    private static <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iterable.iterator(),
                        Spliterator.ORDERED
                ),
                false
        );
    }

    @Transactional
    public List<Tag> upsertTagDtos(List<TagDto> tags) {
        List<String> tagNames = tags.stream().map(TagDto::getName).collect(Collectors.toList());
        createFromTagNames(tagNames);
        return tagRepository.findByNames(tagNames);
    }

    public List<Tag> searchTags(String queryString) {
        if (null == queryString || queryString.isEmpty()) {
            return Collections.emptyList();
        }
        return tagRepository.searchByName(queryString);
    }

    public List<Tag> listTags() {
        return toStream(tagRepository.findAll()).collect(Collectors.toList());
    }

    private void createFromTagNames(List<String> tagNames) {
        Set<String> existingNames = tagRepository.findByNames(tagNames)
                .stream().map(Tag::getName)
                .collect(Collectors.toSet());

        Set<String> tagNamesToPersist = new HashSet<>(tagNames);
        tagNamesToPersist.removeAll(existingNames);

        if (!tagNamesToPersist.isEmpty()) {
            tagRepository.save(tagNamesToPersist.stream()
                    .map(Tag::new)
                    .collect(Collectors.toList()));
        }
    }


}
