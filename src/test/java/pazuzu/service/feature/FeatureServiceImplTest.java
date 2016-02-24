package pazuzu.service.feature;

import org.junit.Before;
import org.junit.Test;
import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepository;
import pazuzu.persistence.FeatureRepositoryInMemoryImpl;
import pazuzu.service.FeatureService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by smohamed on 24/02/16.
 */
public class FeatureServiceImplTest {

    private static Feature FEATURE_JAVA = new Feature("java8", "JDK 8", "", "", null);
    private List<Feature> expectedFeatures = Arrays.asList(
                FEATURE_JAVA,
                new Feature("python", "GO", "", "", null),
                new Feature("go", "GO", "", "", null),
                new Feature("jenkins", "JEEEEEEENKIS", "", "", Arrays.asList(FEATURE_JAVA))

        );

    FeatureRepository featureRepository;
    FeatureService featureService;

    @Before
    public void setUp(){

        featureRepository = mock(FeatureRepositoryInMemoryImpl.class);
        when(featureRepository.getFeatures()).thenReturn(expectedFeatures);

        featureService = new FeatureServiceImpl();
        ((FeatureServiceImpl)featureService).featureRepository = featureRepository;
    }

    @Test
    public void testGetAllFeatures() throws Exception {
        assertEquals("FeatureService does not return all features.", featureRepository.getFeatures(), expectedFeatures);
    }

    @Test
    public void testCreateSortedFeaturelistWithDependencies() throws Exception {

        List<String> requestedFeatures = Arrays.asList("jenkins");
        assertEquals("when requeste feature with one dep. feature list size should be 2", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).size(), 2);
        assertTrue("dependecies are resolved and in the list", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).contains(FEATURE_JAVA));
        assertEquals("dependecies are resolved and in the list in the right order", featureService.createSortedFeaturelistWithDependencies(requestedFeatures).get(0), FEATURE_JAVA);
    }

    @Test
    public void testValidateFeatureNames() throws Exception {
        List<String> requestedFeatures = Arrays.asList("java8", "python", "go", "jenkins");
        assertEquals("Correct feature list is not accepted by featureValidation.", expectedFeatures.size(), requestedFeatures.size());
        assertEquals("Invalid features are not filtered correctly.", featureService.validateFeatureNames(Arrays.asList("INVALID", "java8")).size(), 1);
        assertEquals("List with invalid features only is not of size 0.", featureService.validateFeatureNames(Arrays.asList("INVALID", "ALSO INVALID")).size(), 0);

    }
}