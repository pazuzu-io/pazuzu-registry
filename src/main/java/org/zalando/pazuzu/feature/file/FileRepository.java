package org.zalando.pazuzu.feature.file;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<File, Integer> {
    List<File> findByFeatureNameAndNameIgnoreCaseContaining(String featureName, String namePart);

    List<File> findByFeatureName(String featureName);

    File findOneByFeatureNameAndId(String featureName, Integer id);
}
