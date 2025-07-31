package io.wispforest.accessories_compat.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImmutableWrappingCollection<V, T> extends AbstractCollection<T> implements Set<T> {

    final Set<V> collection;

    final Function<V, T> toCollectionType;
    final BiPredicate<Collection<V>, T> containsCheck;

    public ImmutableWrappingCollection(Set<V> collection, Function<V, T> toCollectionType, BiPredicate<Collection<V>, T> containsCheck) {
        this.collection = collection;
        this.toCollectionType = toCollectionType;
        this.containsCheck = containsCheck;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    //--

    @Override
    public boolean contains(Object o) {
        try {
            return containsCheck.test(this.collection, (T) o);
        } catch (Exception e) {
            return false;
        }
    }

    //--

    @Override
    public Iterator<T> iterator() {
        if (this.collection.isEmpty()) return Collections.emptyIterator();

        var itr = this.collection.iterator();

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public T next() {
                return ImmutableWrappingCollection.this.toCollectionType.apply(itr.next());
            }
        };
    }

    @Override
    public int size() {
        return this.collection.size();
    }
}
