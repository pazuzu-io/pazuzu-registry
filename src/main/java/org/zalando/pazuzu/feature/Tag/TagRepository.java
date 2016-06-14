package org.zalando.pazuzu.feature.tag;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by vpavlyshyn on 10/06/16.
 */
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "SELECT t FROM Tag t WHERE t.name IN :names")
    List<Tag> findByNames(@Param("names") List<String> names);

    @Query(value = "SELECT t FROM Tag t WHERE t.name like %:query")
    List<Tag> searchByName(@Param("query") String query);
}
