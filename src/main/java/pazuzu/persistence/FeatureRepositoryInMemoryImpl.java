package pazuzu.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
@Repository
public class FeatureRepositoryInMemoryImpl implements FeatureRepository {

	private static HashMap<String, Feature> store = new HashMap<>();

	// Bootstrap
	static {
		store.put("java8", new Feature("java8", "JDK 8", "", "", null));
		store.put("java7", new Feature("java7", "JDK 7", "", "", null));
		store.put("python2", new Feature("python2", "Python 2.7 interpreter", "", "", null));
		store.put("python3", new Feature("python3", "Python 3.5 interpreter", "", "", null));
		store.put("ssh", new Feature("ssh", "SSH server", "", "", null));
		store.put("npm", new Feature("npm", "Dependency manager", "", "", null));
		store.put("maven", new Feature("maven", "Maven 3", "", "", null));
		store.put("gradle", new Feature("gradle", "Gradle", "", "", null));
		store.put("bower", new Feature("bower", "Dependency manager for the web", "", "", null));
		store.put("ruby", new Feature("ruby", "Ruby interpreter", "", "", null));
	}

	@Override
	public List<Feature> getFeatures() {
		return new ArrayList<Feature>(store.values());
	}
}
