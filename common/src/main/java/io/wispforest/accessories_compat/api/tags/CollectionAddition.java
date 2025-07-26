package io.wispforest.accessories_compat.api.tags;

import java.util.Collection;

public interface CollectionAddition<T> {
    void add(T entry);

    void add(Collection<T> entries);
}
