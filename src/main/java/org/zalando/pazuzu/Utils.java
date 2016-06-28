package org.zalando.pazuzu;

import java.util.stream.Collector;
import java.util.stream.StreamSupport;

public final class Utils {
    public static <T, A, R> R collect(Iterable<T> source, Collector<T, A, R> collector) {
        return StreamSupport.stream(source.spliterator(), false)
                .collect(collector);
    }
}
