package org.zalando.pazuzu.registry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.pazuzu.AbstractComponentTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test registry repository.
 * As there is only read operations on the registry entry, the test depend on the flyway script to populate the table.
 */
public class RegistryTest extends AbstractComponentTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryTest.class);
    @Autowired
    private RegistryRepository repository;

    @Test
    public void testFindOne() {
        Registry registry = repository.findOne(1);
        assertNotNull(registry);
        LOGGER.info("Registry findOne: {}", registry);
    }

    @Test
    public void testFindAll() {
        Iterable<Registry> registries = repository.findAll();
        assertNotNull(registries);
        assertTrue(registries.iterator().hasNext());
        LOGGER.info("Registry findAll: {}", registries);
    }
}
