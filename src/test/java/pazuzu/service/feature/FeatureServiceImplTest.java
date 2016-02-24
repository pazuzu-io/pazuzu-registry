package pazuzu.service.feature;

import org.junit.Before;
import org.junit.Test;
import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepository;
import pazuzu.persistence.FeatureRepositoryInMemoryImpl;
import pazuzu.service.FeatureService;
import test.data.TestFeatures;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by smohamed on 24/02/16.
 */
public class FeatureServiceImplTest {

    FeatureRepository featureRepository;
    FeatureService featureService;

    @Before
    public void setUp(){

        featureRepository = mock(FeatureRepositoryInMemoryImpl.class);
        when(featureRepository.getFeatures()).thenReturn(TestFeatures.FEATURES);

        featureService = new FeatureServiceImpl();
        ((FeatureServiceImpl)featureService).featureRepository = featureRepository;
    }

    @Test
    public void testGetAllFeatures() throws Exception {
        assertEquals("FeatureService does not return all features.", featureRepository.getFeatures(), TestFeatures.FEATURES);
    }

    @Test
    public void testCreateSortedFeaturelistWithDependencies() throws Exception {

        List<String> requestedFeatures = Arrays.asList("jenkins");
        assertEquals("when requeste feature with one dep. feature list size should be 2", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).size(), 2);
        assertTrue("dependecies are resolved and in the list", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).contains(TestFeatures.FEATURE_JAVA));
        assertEquals("dependecies are resolved and in the list in the right order", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).get(0), TestFeatures.FEATURE_JAVA);
    }

    @Test
    public void testValidateFeatureNames() throws Exception {
        List<String> validFeatureNames = TestFeatures.FEATURES.stream()
                .map(Feature::getName)
                .collect(Collectors.toList());

        assertEquals("Correct feature list is not accepted by featureValidation.",
                TestFeatures.FEATURES.size(),
                validFeatureNames.size());
        assertEquals("Invalid features are not filtered correctly.",
                featureService.validateFeatureNames(Arrays.asList("INVALID", TestFeatures.FEATURE_JAVA.name)).size(),
                1);
        assertEquals("List with invalid features only is not of size 0.",
                featureService.validateFeatureNames(Arrays.asList("INVALID", "ALSO INVALID")).size(),
                0);

    }
}