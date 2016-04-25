package org.zalando.pazuzu.sort;

import java.util.*;
import java.util.function.Function;

/**
 * Topological sort algorithm
 * Running time complexity O(n + m)
 * where n - number of vertices, m - number of edges
 */
public class TopologicalSortLinear<T> {

    /**
     * VisitState enum for vertex
     * WHITE - not visited
     * GREY - visited in process of traversal
     * BLACK - visited
     */
    private enum VisitState {
        NOT_VISITED, BEING_VISITED, VISITED
    }

    // List of all vertices
    private Collection<T> vertices;

    // Function for returning all children of vertex
    private Function<T, Set<T>> getChildren;

    // Visited indicator for vertex
    private Map<T, VisitState> color;

    // Parent of vertex
    // key - vertex, value - parent of vertex
    private Map<T, T> parent;

    public TopologicalSortLinear(Collection<T> vertices, Function<T, Set<T>> getChildren) {
        this.vertices = vertices;
        this.getChildren = getChildren;

        this.color = new HashMap<>(vertices.size());
        // mark all vertices as not visited
        for(T v : vertices) {
            color.put(v, VisitState.NOT_VISITED);
        }

        this.parent = new HashMap<>();
    }

    /**
     * @return topologically sorted list of items
     * @throws IllegalStateException in case of cycle in graph
     */
    public List<T> getTopSorted() {
        List<T> topSort = new LinkedList<>();

        getVertices().forEach(v -> {
            VisitState clr = getColor(v);
            if (clr.equals(VisitState.NOT_VISITED)) {
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
        setColor(v, VisitState.BEING_VISITED);

        getChildren.apply(v).forEach(child -> {
            VisitState clr = getColor(child);
            if (clr.equals(VisitState.NOT_VISITED)) {
                dfs(child, v, topSort);
            } else if (clr.equals(VisitState.BEING_VISITED)) {
                throw new IllegalStateException("Cycle found in dependencies! " + getCycle(v));
            }
        });

        setColor(v, VisitState.VISITED);
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

    /**
     * @return List of all vertices
     */
    private Collection<T> getVertices() {
        return this.vertices;
    }

    /**
     * @return VisitState of v
     */
    private VisitState getColor(T v) {
        return color.get(v);
    }

    /**
     * @param v Vertex to be colored
     * @param clr VisitState of vertex v
     */
    private void setColor(T v, VisitState clr) {
        color.put(v, clr);
    }
}
