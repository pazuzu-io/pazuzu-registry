package pazuzu.service.graph;
import pazuzu.model.Feature;
import java.util.HashSet;

/**
 * Created by smohamed on 22/02/16.
 */
public class Node{
    public Feature feature;
    public HashSet<Edge> inEdges;
    public HashSet<Edge> outEdges;
    public Node(Feature feature) {
        this.feature = feature;
        inEdges = new HashSet<Edge>();
        outEdges = new HashSet<Edge>();
    }
    public Node addEdge(Node node){
        Edge e = new Edge(this, node);
        outEdges.add(e);
        node.inEdges.add(e);
        return this;
    }
    @Override
    public String toString() {
        return feature.toString();
    }
}