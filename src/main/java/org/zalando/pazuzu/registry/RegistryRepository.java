package org.zalando.pazuzu.registry;

import org.springframework.data.repository.Repository;

/**
 * Simple find one repository for Registry as currently Registry is read only.
 */
public interface RegistryRepository extends Repository<Registry, Integer> {
    Registry findOne(Integer id);
    Iterable<Registry> findAll();
}
