package org.zalando.pazuzu.feature;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

public class FeatureComparatorTest {

    @Test
    public void testVersionOrder() {
        List<Feature> features = Lists.newArrayList(
                new Feature().setId(1).setName("mylibrary").setVersion("1.0.1"),
                new Feature().setId(2).setName("mylibrary").setVersion("1.1"),
                new Feature().setId(3).setName("mylibrary").setVersion("1.0"),
                new Feature().setId(4).setName("mylibrary").setVersion("1.2"),
                new Feature().setId(5).setName("mylibrary").setVersion("1.0.2"),
                new Feature().setId(6).setName("mylibrary").setVersion("2.0.3"),
                new Feature().setId(7).setName("mylibrary").setVersion("3.0"),
                new Feature().setId(8).setName("mylibrary").setVersion("1.1.1"),
                new Feature().setId(9).setName("mylibrary").setVersion("2.2.1")
        ).stream().sorted(new FeatureComparator().reversed()).collect(Collectors.toList());

        List<Feature> sortedFeatures = Lists.newArrayList(
                new Feature().setId(7).setName("mylibrary").setVersion("3.0"),
                new Feature().setId(9).setName("mylibrary").setVersion("2.2.1"),
                new Feature().setId(6).setName("mylibrary").setVersion("2.0.3"),
                new Feature().setId(4).setName("mylibrary").setVersion("1.2"),
                new Feature().setId(8).setName("mylibrary").setVersion("1.1.1"),
                new Feature().setId(2).setName("mylibrary").setVersion("1.1"),
                new Feature().setId(5).setName("mylibrary").setVersion("1.0.2"),
                new Feature().setId(1).setName("mylibrary").setVersion("1.0.1"),
                new Feature().setId(3).setName("mylibrary").setVersion("1.0")
        );

        assertEquals(sortedFeatures, features);
    }

}
