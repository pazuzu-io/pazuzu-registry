package org.zalando.pazuzu.sort;

import java.util.*;
import java.util.function.Function;

public class TopologicalSortLinear<T> {

    private Collection<T> vertices;
    // Function for returning all children of vertex
    private Function<T, Set<T>> getChildren;
    // Visit state indicator for vertex
    private Map<T, VisitState> vertexState;
    // Parent of vertex
    // key - vertex, value - parent of vertex
    private Map<T, T> parent;

    public TopologicalSortLinear(Collection<T> vertices, Function<T, Set<T>> getChildren) {
        this.vertices = vertices;
        this.getChildren = getChildren;

        this.vertexState = new HashMap<>(vertices.size());
        // mark all vertices as not visited
        for (T v : vertices) {
            vertexState.put(v, VisitState.NOT_VISITED);
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
            VisitState clr = getVisitState(v);
            if (clr.equals(VisitState.NOT_VISITED)) {
                depthFirstSearch(v, null, topSort);
            }
        });

        return topSort;
    }

    /**
     * Depth first search algorithm
     *
     * @param v        Current vertex in graph traversal
     * @param ancestor Parent of v (null if v is root)
     * @param topSort  Topologically sorted vertices so far
     */
    private void depthFirstSearch(T v, T ancestor, List<T> topSort) {
        if (parent != null) {
            parent.put(v, ancestor);
        }
        setVisitState(v, VisitState.BEING_VISITED);

        getChildren.apply(v).forEach(child -> {
            VisitState clr = getVisitState(child);
            if (clr.equals(VisitState.NOT_VISITED)) {
                depthFirstSearch(child, v, topSort);
            } else if (clr.equals(VisitState.BEING_VISITED)) {
                throw new IllegalStateException("Cycle found in dependencies! " + getCycle(v));
            }
        });

        setVisitState(v, VisitState.VISITED);
        topSort.add(v);
    }

    /**
     * @param v Last vertex in cycle
     * @return List of items that form the cycle
     */
    private List<T> getCycle(T v) {
        List<T> cycle = new LinkedList<>();
        T current = v;
        while (current != null) {
            cycle.add(current);
            if (parent.containsKey(current)) {
                current = parent.get(current);
            } else {
                current = null;
            }
        }
        return cycle;
    }

    private Collection<T> getVertices() {
        return this.vertices;
    }

    private VisitState getVisitState(T v) {
        return vertexState.get(v);
    }

    private void setVisitState(T v, VisitState clr) {
        vertexState.put(v, clr);
    }

    private enum VisitState {
        NOT_VISITED, BEING_VISITED, VISITED
    }
}
