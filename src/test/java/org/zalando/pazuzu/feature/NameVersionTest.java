package org.zalando.pazuzu.feature;

import org.junit.Test;
import org.zalando.pazuzu.exception.FeatureNameEmptyException;

import java.util.Arrays;

import static org.junit.Assert.*;

public class NameVersionTest {

    @Test
    public void testName() {
        String []parts = "8.1.1".split("\\.");
        System.out.println(Arrays.toString(parts));

        String name = "java";
        NameVersion nameVersion = NameVersion.parse(name);
        assertEquals(name, nameVersion.name());
    }

    @Test
    public void testNameAndVersion() {
        String name = "java:8.121";
        NameVersion nameVersion = NameVersion.parse(name);
        assertEquals("java", nameVersion.name());
        assertTrue(nameVersion.version().isPresent());
        assertEquals("8.121", nameVersion.version().get());
    }

    @Test
    public void testNameAndColon() {
        String name = "java:";
        NameVersion nameVersion = NameVersion.parse(name);
        assertEquals("java", nameVersion.name());
    }

    @Test(expected = FeatureNameEmptyException.class)
    public void testEmptyString() {
        String name = "";
        NameVersion.parse(name);
    }

    @Test(expected = FeatureNameEmptyException.class)
    public void testOnlyColon() {
        String name = ":";
        NameVersion.parse(name);
    }

}
