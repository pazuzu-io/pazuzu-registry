package pazuzu.service.graph;
import java.util.ArrayList;

/**
 * Created by smohamed on 22/02/16.
 */
public class Graph {
    ArrayList<Node> graph;
    public Graph(ArrayList<Node> features){
        this.graph = new ArrayList<Node>(features);
    }

}
