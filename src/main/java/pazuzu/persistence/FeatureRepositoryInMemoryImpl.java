package pazuzu.persistence;

import java.util.*;

import org.springframework.stereotype.Repository;

import pazuzu.model.Feature;

/**
 * Created by cseidel on 22/02/16.
 */
@Repository
public class FeatureRepositoryInMemoryImpl implements FeatureRepository {

	private static HashMap<String, Feature> store = new HashMap<>();

	public static Feature JAVA8_FEATURE = new Feature("java8", "JDK 8", "", "", null);
	// Bootstrap
	static {
		store.put("java8", JAVA8_FEATURE);
		store.put("java7", new Feature("java7", "JDK 7", "", "", null));
		store.put("python2", new Feature("python2", "Python 2.7 interpreter", "", "", null));
		store.put("python3", new Feature("python3", "Python 3.5 interpreter", "", "", null));
		store.put("ssh", new Feature("ssh", "SSH server", "", "", null));
		store.put("npm", new Feature("npm", "Dependency manager", "", "", null));
		store.put("maven", new Feature("maven", "Maven 3", "", "", Arrays.asList(JAVA8_FEATURE)));
		store.put("gradle", new Feature("gradle", "Gradle", "", "", null));
		store.put("bower", new Feature("bower", "Dependency manager for the web", "", "", null));
		store.put("ruby", new Feature("ruby", "Ruby interpreter", "", "", null));
	}

	@Override
	public List<Feature> getFeatures() {
		return new ArrayList<Feature>(store.values());
	}
}
