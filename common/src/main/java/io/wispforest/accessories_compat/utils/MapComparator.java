package io.wispforest.accessories_compat.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

public record MapComparator<K, V>(Comparator<K> keyComparator, Comparator<V> valueComparator, Comparator<Map.Entry<K, V>> entryComparator) {

    public static <K extends Comparable<K>, V> MapComparator<K, V> of(Function<V, K> valueToKey) {
        Comparator<K> keyComparator = Comparator.naturalOrder();

        return new MapComparator<>(
            keyComparator,
            (o1, o2) -> keyComparator.compare(valueToKey.apply(o1), valueToKey.apply(o2)),
            (o1, o2) -> keyComparator.compare(o1.getKey(), o2.getKey())
        );
    }
}
