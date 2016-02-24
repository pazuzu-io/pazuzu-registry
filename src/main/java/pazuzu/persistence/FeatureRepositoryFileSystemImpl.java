package pazuzu.persistence;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import pazuzu.model.Feature;

@Repository
public class FeatureRepositoryFileSystemImpl implements FeatureRepository {

	private static final Logger logger = LoggerFactory.getLogger(FeatureRepositoryFileSystemImpl.class);

	@Value("${features.store.path}")
	private String storePathStr;

	@Autowired
	private ObjectMapper jsonMapper;

	@Override
	public Collection<Feature> getFeatures() {
		Path storePath = loadOrCreateStoreDir();
		return featuresExtract(storePath);
	}

	private Path loadOrCreateStoreDir() {
		Path storePath = Paths.get(storePathStr);

		if (Files.exists(storePath)) {
			if (!Files.isDirectory(storePath)) {
				throw new RuntimeException(storePath + " should be a directory");
			}
		} else {
			try {
				Files.createDirectory(storePath);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		return storePath;
	}

	private Collection<Feature> featuresExtract(Path storePath) {
		HashSet<Feature> features = new HashSet<>();

		try (DirectoryStream<Path> storeDir = Files.newDirectoryStream(storePath)) {
			for (Path featurePath : storeDir) {
				try {
					features.add(jsonMapper.readValue(Files.newInputStream(featurePath), Feature.class));
				} catch (Exception ex) {
					logger.warn("Unable to load feature " + featurePath, ex);
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return features;
	}

	public String getStorePathStr() {
		return storePathStr;
	}

	public void setStorePathStr(String storePathStr) {
		this.storePathStr = storePathStr;
	}

	public ObjectMapper getJsonMapper() {
		return jsonMapper;
	}

	public void setJsonMapper(ObjectMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

}
