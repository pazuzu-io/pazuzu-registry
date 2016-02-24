package test.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import pazuzu.model.Feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cseidel on 23/02/16.
 */
public class TestFeatures {

    public static Feature FEATURE_JAVA = new Feature("java8", "JDK 8", "", "", null);
    public static List<Feature> FEATURES = Arrays.asList(
            FEATURE_JAVA,
            new Feature("python", "GO", "", "", null),
            new Feature("go", "GO", "", "", null),
            new Feature("jenkins", "JEEEEEEENKIS", "", "", Arrays.asList(FEATURE_JAVA))

    );
}
