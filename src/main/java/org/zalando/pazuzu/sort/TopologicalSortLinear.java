package org.zalando.pazuzu.sort;

import java.util.*;
import java.util.function.Function;

/**
 * Created by dimangaliyev on 25/04/16.
 * Topological sort algorithm
 * Running time complexity O(n + m)
 * where n - number of vertices, m - number of edges
 */
public class TopologicalSortLinear<T> {

    /**
     * Color enum for vertex
     * WHITE - not visited
     * GREY - visited in process of traversal
     * BLACK - visited
     */
    private enum Color {
        WHITE, GREY, BLACK
    }

    // List of all vertices
    private Collection<T> vertices;

    // Function for returning all children of vertex
    private Function<T, Set<T>> getChildren;

    // Visited indicator for vertex
    private Map<T, Color> color;

    // Parent of vertex
    // key - vertex, value - parent of vertex
    private Map<T, T> parent;

    public TopologicalSortLinear(Collection<T> vertices, Function<T, Set<T>> getChildren) {
        this.vertices = vertices;
        this.getChildren = getChildren;

        this.color = new HashMap<>(vertices.size());
        // mark all vertices as not visited
        for(T v : vertices) color.put(v, Color.WHITE);

        this.parent = new HashMap<>();
    }

    /**
     * @return List of all vertices
     */
    private Collection<T> getVertices() {
        return this.vertices;
    }

    /**
     * @return Color of v
     */
    private Color getColor(T v) {
        return color.get(v);
    }

    /**
     * @param v Vertex to be colored
     * @param clr Color of vertex v
     */
    private void setColor(T v, Color clr) {
        color.put(v, clr);
    }

    /**
     * @return topologically sorted list of items
     * @throws IllegalStateException in case of cycle in graph
     */
    public List<T> getTopSorted() {
        List<T> topSort = new LinkedList<>();

        getVertices().forEach(v -> {
            Color clr = getColor(v);
            if (clr.equals(Color.WHITE)) {
                dfs(v, null, topSort);
            }
        });

        Collections.reverse(topSort);
        return topSort;
    }

    /**
     * Depth first search algorithm
     * @param v Current vertex in graph traversal
     * @param ancestor Parent of v (null if v is root)
     * @param topSort Topologically sorted vertices so far
     */
    private void dfs(T v, T ancestor, List<T> topSort) {
        if (parent != null) {
            parent.put(v, ancestor);
        }
        setColor(v, Color.GREY);

        getChildren.apply(v).forEach(child -> {
            Color clr = getColor(child);
            if (clr.equals(Color.WHITE)) {
                dfs(child, v, topSort);
            } else if (clr.equals(Color.GREY)) {
                throw new IllegalStateException("Cycle found in dependencies! " + getCycle(v));
            }
        });

        setColor(v, Color.BLACK);
        topSort.add(v);
    }

    /**
     * @param v Last vertex in cycle
     * @return List of items that form the cycle
     */
    private List<T> getCycle(T v) {
        List<T> cycle = new LinkedList<>();
        while(v != null) {
            cycle.add(v);
            if (parent.containsKey(v)) {
                v = parent.get(v);
            } else {
                v = null;
            }
        }
        return cycle;
    }
}
