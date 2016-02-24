package pazuzu.service.graph;

import pazuzu.model.Feature;
import java.util.*;

/**
 * Created by smohamed on 22/02/16.
 */
public class Graph {
    HashMap<String, Node> graphMap;
    public Graph(){
        graphMap = new HashMap<>();
    }
    public void buildGraph(Collection<Feature> features) {
        //iterates over all features and adds them to the graph
        for (Feature feat : features) {
            Node node = new Node(feat.name);
            graphMap.put(feat.name, node);
        }

        System.out.println(graphMap.toString());
        //iterates over the dependencies of each feature and adds it to the node of that feature
        for (Feature feat : features) {
            if (feat.dependencies == null) continue;
            for (Feature dep : feat.dependencies) {
                graphMap.get(feat.name).addEdge(graphMap.get(dep.name));
            }
        }
    }
    public void DisplayGraph(){
        for (String key:graphMap.keySet()){
            System.out.print(key+" -> in Edges:");
            for (Edge e: graphMap.get(key).inEdges)
                System.out.print(e.toString());
            System.out.print(" out Edges: ");
            for (Edge e: graphMap.get(key).outEdges)
                System.out.print(e.toString());
        }
    }

    public ArrayList<String> getFeatureNamesFromFeatures(ArrayList<Feature> features){
        ArrayList<String> featureNames = new ArrayList<>();
        for(Feature f: features){
            featureNames.add(f.name);
        }
        return featureNames;
    }


    public List<String> Tsort(List<String> root)
    {

        System.out.println("our list" + root.toString());
        ArrayList<Node> allNodes = new ArrayList<>();
        for (String feat:root){
            allNodes.add(graphMap.get(feat));
        }

        //L <- Empty list that will contain the sorted elements
        ArrayList<Node> L = new ArrayList<Node>();

        //S <- Set of all nodes with no incoming edges
        HashSet<Node> S = new HashSet<Node>();
        for(Node n : allNodes){
            if(n.inEdges.size() == 0){
                S.add(n);
            }
            //else we would just add it to the sorted list first because there's no dependencies
            else
                L.add(n);
        }

        //while S is non-empty do
        while(!S.isEmpty()){
            //remove a node n from S
            Node n = S.iterator().next();
            S.remove(n);

            //insert n into L
            L.add(n);

            //for each node m with an edge e from n to m do
            for(Iterator<Edge> it = n.outEdges.iterator(); it.hasNext();){

                //remove edge e from the graph
                Edge e = it.next();
                Node m = e.to;
                it.remove();//Remove edge from n
                m.inEdges.remove(e);//Remove edge from m

                //if m has no other incoming edges then insert m into S
                if(m.inEdges.isEmpty()){
                    S.add(m);
                }
            }
        }

        ArrayList<String> sortedFeatureNames = new ArrayList<>();
        for (Node n:L){
            sortedFeatureNames.add(n.feature_name);
        }

        Collections.reverse(sortedFeatureNames);
        return sortedFeatureNames;

    }

}
