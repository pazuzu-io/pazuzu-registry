package pazuzu.service.graph;

import org.junit.Before;
import org.junit.Test;
import test.data.TestFeatures;

import static org.junit.Assert.*;

/**
 * Created by smohamed on 24/02/16.
 */
public class GraphTest {

    Graph graph;
    @Before
    public void setUp() throws Exception {
        graph = new Graph();
    }

    @Test
    public void testBuildGraph() throws Exception {
        graph.buildGraph(TestFeatures.FEATURES);

    }

    @Test
    public void testGetFeatureNamesFromFeatures() throws Exception {

    }

    @Test
    public void testTsort() throws Exception {

    }
}