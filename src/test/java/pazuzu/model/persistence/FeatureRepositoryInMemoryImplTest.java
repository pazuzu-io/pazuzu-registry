package pazuzu.model.persistence;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepositoryInMemoryImpl;

public class FeatureRepositoryInMemoryImplTest {

	private FeatureRepositoryInMemoryImpl featureRepository = new FeatureRepositoryInMemoryImpl();

	@Test
	public void getFeaturesShouldReturnBootstrappedList() {
		// Act
		Collection<Feature> features = featureRepository.getFeatures();

		// Assert
		assertThat(features, is(notNullValue()));
		assertThat(features.size(), is(10));
	}
}
