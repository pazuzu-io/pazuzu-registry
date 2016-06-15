package org.zalando.pazuzu.sort;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TopologicalSortLinearTest {

    // Initialize vertices
    private Collection<Integer> initVertices(int n) {
        List<Integer> vertices = new LinkedList<>();
        for (int i = 0; i < n; ++i) vertices.add(i);

        return vertices;
    }

    // Initialize adjacency matrix for graph
    private Set<Integer>[] initGraph(int n) {
        Set<Integer> graph[] = new HashSet[n];
        for (int i = 0; i < n; ++i) graph[i] = new HashSet<>();

        return graph;
    }

    @Test
    public void whenGivenGraphItShouldTopSortItCase1() {
        int n = 5;
        Collection<Integer> vertices = initVertices(n);
        Set<Integer>[] graph = initGraph(n);
        for (int i = 0; i + 1 < n; ++i) graph[i].add(i + 1);

        TopologicalSortLinear<Integer> sorted = new TopologicalSortLinear<>(vertices, (v) -> graph[v]);
        List<Integer> actual = sorted.getTopSorted();

        List<Integer> expected = Arrays.asList(4, 3, 2, 1, 0);
        assertThat(actual, is(expected));
    }

    @Test
    public void whenGivenGraphItShouldTopSortItCase2() {
        int n = 1;
        Collection<Integer> vertices = initVertices(n);
        Set<Integer>[] graph = initGraph(n);

        TopologicalSortLinear<Integer> sorted = new TopologicalSortLinear<>(vertices, (v) -> graph[v]);
        List<Integer> actual = sorted.getTopSorted();

        List<Integer> expected = Arrays.asList(0);
        assertThat(actual, is(expected));
    }

    @Test
    public void whenGivenGraphItShouldTopSortItCase3() {
        int n = 5;
        Collection<Integer> vertices = initVertices(n);
        Set<Integer>[] graph = initGraph(n);

        graph[1].add(3);
        graph[3].add(2);
        graph[2].add(0);
        graph[2].add(4);

        TopologicalSortLinear<Integer> sorted = new TopologicalSortLinear<>(vertices, (v) -> graph[v]);
        List<Integer> actual = sorted.getTopSorted();
        // Both [1, 3, 2, 4, 0], [1, 3, 2, 0, 4] are valid
        List<Integer> expected = Arrays.asList(0, 4, 2, 3, 1);
        assertThat(actual, is(expected));
    }

    @Test(expected = IllegalStateException.class)
    public void whenGivenGraphWithCycleItShouldThrowExceptionCase1() {
        int n = 5;
        Collection<Integer> vertices = initVertices(n);
        Set<Integer>[] graph = initGraph(n);

        // Cycle
        graph[0].add(1);
        graph[1].add(2);
        graph[2].add(3);
        graph[3].add(4);
        graph[4].add(0);

        graph[0].add(2);
        graph[2].add(4);

        TopologicalSortLinear<Integer> sorted = new TopologicalSortLinear<>(vertices, (v) -> graph[v]);
        sorted.getTopSorted();
    }

    @Test
    public void whenGivenGraphWithCycleItShouldThrowExceptionCase2() {
        int n = 5;
        Collection<Integer> vertices = initVertices(n);
        Set<Integer>[] graph = initGraph(n);

        // Cycle
        graph[0].add(1);
        graph[1].add(2);
        graph[2].add(3);
        graph[3].add(4);
        graph[4].add(0);

        graph[0].add(2);
        graph[2].add(4);

        List<Integer> cycle = new LinkedList<>();
        cycle.add(4);
        cycle.add(3);
        cycle.add(2);
        cycle.add(1);
        cycle.add(0);

        TopologicalSortLinear<Integer> sorted = new TopologicalSortLinear<>(vertices, (v) -> graph[v]);
        try {
            sorted.getTopSorted();
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Cycle found in dependencies! " + cycle);
        }
    }
}
