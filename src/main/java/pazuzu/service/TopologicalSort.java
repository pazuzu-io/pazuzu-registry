package pazuzu.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Sort partially ordered elements.
 * More info available at https://en.wikipedia.org/wiki/Topological_sorting
 */
public class TopologicalSort {
    private static class Node<T> {
        public final T value;
        public final Set<Node<T>> incoming = new HashSet<>();
        public final Set<Node<T>> outgoing = new HashSet<>();

        public Node(T value) {
            this.value = value;
        }
    }

    /**
     * @param items Items to sort
     * @param containsFunc contains(a, b) = True if a contains b
     * @param <T> Type of elements
     * @return Topologically sorted list
     * @throws IllegalStateException in case of recursive dependencies
     */
    @SuppressWarnings("unused")
    public static <T> List<T> sort(Collection<T> items, BiFunction<T, T, Boolean> containsFunc) {
        final List<Node<T>> nodes = items.stream().map(Node::new).collect(Collectors.toList());
        for (int i = 0; i < nodes.size(); ++i) {
            final Node<T> checkedNode = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); ++j) {
                final Node<T> second = nodes.get(j);
                if (containsFunc.apply(checkedNode.value, second.value)) {
                    checkedNode.outgoing.add(second);
                    second.incoming.add(checkedNode);
                } else if (containsFunc.apply(second.value, checkedNode.value)) {
                    checkedNode.incoming.add(second);
                    second.outgoing.add(checkedNode);
                }
            }
        }
        final List<T> result = new ArrayList<>();
        while (!nodes.isEmpty()) {
            final List<Node<T>> toRemove = nodes.stream().filter(n -> n.outgoing.size() == 0).collect(Collectors.toList());
            if (toRemove.isEmpty()) {
                throw new IllegalStateException("Recursion found! can't do anything with nodes " + nodes.stream().map(n -> n.value).collect(Collectors.toList()));
            }
            toRemove.forEach(node -> {
                node.incoming.forEach(item -> item.outgoing.remove(node));
                result.add(node.value);
            });
            nodes.removeAll(toRemove);
        }
        return result;
    }
}
