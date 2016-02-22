package pazuzu.service.graph;
import pazuzu.model.Feature;
import java.util.HashSet;

/**
 * Created by smohamed on 22/02/16.
 */
public class Node{
    public String feature_name;
    public HashSet<Edge> inEdges;
    public HashSet<Edge> outEdges;
    public Node(String feature_name) {
        this.feature_name = feature_name;
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
        return feature_name;
    }
}