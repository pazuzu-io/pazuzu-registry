package pazuzu.model.persistence;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import pazuzu.model.Feature;
import pazuzu.persistence.FeatureRepositoryFileSystemImpl;

public class FeatureRepositoryFileSystemImplTest {

	private FeatureRepositoryFileSystemImpl featureRepository = new FeatureRepositoryFileSystemImpl();

	@Test
	public void getFeaturesShouldReturnFeaturePerFile() throws Exception {
		// Arrange
		Path pazuzuTestPath = Paths.get(System.getProperty("user.home") + "/.pazuzu/test/features");

		Feature feature1 = new Feature("feature1", "feature1", "", "", null);
		Feature feature2 = new Feature("feature2", "feature2", "", "", Arrays.asList(feature1));

		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.writeValue(new FileOutputStream(pazuzuTestPath.resolve(feature1.name).toFile()), feature1);
		jsonMapper.writeValue(new FileOutputStream(pazuzuTestPath.resolve(feature2.name).toFile()), feature2);

		featureRepository.setStorePathStr(pazuzuTestPath.toString());
		featureRepository.setJsonMapper(jsonMapper);

		// Act
		Collection<Feature> features = featureRepository.getFeatures();

		// Assert
		assertThat(features, is(notNullValue()));
		assertThat(features.size(), is(2));
	}

	@Test
	public void getFeaturesShouldThrowExceptionWhenStoreFolderIsNotADirectory() throws IOException {
		// Arrange
		Path pazuzuTestPath = Files.createTempFile("pazuzutest", null);
		pazuzuTestPath.toFile().deleteOnExit();

		featureRepository.setStorePathStr(pazuzuTestPath.toString());

		// Act
		catchException(featureRepository).getFeatures();

		// Assert
		assertThat(caughtException(), is(notNullValue()));
		assertThat(caughtException().getMessage(), containsString("should be a directory"));
	}

	@Test
	public void getFeaturesShouldCreateStoreFolderWhenNotExisting() throws IOException {
		// Arrange
		Path pazuzuTestPath = Paths.get(System.getProperty("user.home") + "/.pazuzu/test/non_existing_store");
		assertThat(Files.exists(pazuzuTestPath), is(false));

		featureRepository.setStorePathStr(pazuzuTestPath.toString());

		// Act
		featureRepository.getFeatures();

		// Assert
		assertThat(Files.isDirectory(pazuzuTestPath), is(true));

		// Teardown
		Files.delete(pazuzuTestPath);
	}
}
