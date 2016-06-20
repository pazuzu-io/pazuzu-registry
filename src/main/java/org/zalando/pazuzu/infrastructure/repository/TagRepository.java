package org.zalando.pazuzu.infrastructure.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.zalando.pazuzu.infrastructure.domain.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "SELECT t FROM Tag t WHERE t.name IN :names")
    List<Tag> findByNames(@Param("names") List<String> names);

    @Query(value = "SELECT t FROM Tag t WHERE t.name like CONCAT(:query, '%') ")
    List<Tag> searchByName(@Param("query") String query);
}
