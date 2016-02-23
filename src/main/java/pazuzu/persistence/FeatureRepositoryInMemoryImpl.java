package pazuzu.persistence;

import java.util.List;

import org.springframework.stereotype.Service;
import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
@Service
public class FeatureRepositoryInMemoryImpl implements FeatureRepository {

	@Override
	public List<Feature> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}
}
