package pazuzu.service.graph;

/**
 * Created by smohamed on 22/02/16.
 */
public class Edge {
    public final Node from;
    public final Node to;
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }
    @Override
    public boolean equals(Object obj) {
        Edge e = (Edge)obj;
        return e.from == from && e.to == to;
    }

    @Override
    public String toString(){
        return "From: "+this.from + " To: "+ this.to;
    }
}
