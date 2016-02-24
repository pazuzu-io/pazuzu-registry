package test.config;

import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepository;
import pazuzu.persistence.FeatureRepositoryInMemoryImpl;
import pazuzu.service.FeatureService;
import pazuzu.service.feature.FeatureServiceImpl;
import pazuzu.web.controller.FeatureRefsController;
import test.data.TestFeatures;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by cseidel on 23/02/16.
 *
 * Spring Config for testing purposes
 */
public class SpringTestConfig {

    @Bean
    public FeatureService getFeatureService(){
        FeatureService mockedFeatureService = mock(FeatureServiceImpl.class);
        when(mockedFeatureService.getAllFeatures()).thenReturn(TestFeatures.FEATURES);
        return mockedFeatureService;
    }

    @Bean
    public FeatureRepository getFeatureRepository(){
        FeatureRepository featureRepository = mock(FeatureRepositoryInMemoryImpl.class);
        when(featureRepository.getFeatures()).thenReturn(TestFeatures.FEATURES);
        return featureRepository;
    }

    @Bean
    public FeatureRefsController getFeatureRefsController(){
        FeatureRefsController featureRefsController = new FeatureRefsController();
        return featureRefsController;
    }
}
