package org.zalando.pazuzu.feature.file;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<File, Integer> {

    List<File> findByNameIgnoreCaseContaining(String name);

    File findByName(String name);
}
