package io.wispforest.accessories_compat.utils;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.*;

public final class ImmutableDelegatingMap<K, V, I> implements Map<K, V> {

    private static final Logger LOGGER = LogUtils.getLogger();

    public final String debugNaming;
    private Runnable errorMessage;

    public final Class<K> keyClass;
    public final Class<V> valueClass;

    public final Map<K, I> map;

    public final UnaryOperator<K> toKeyNamespace;
    public final UnaryOperator<K> fromKeyNamespace;

    public final BiFunction<K, I, V> toValueMapFunc;
    public final Function<V, @Nullable I> fromValueMapFunc;

    public ImmutableDelegatingMap(
        String debugNaming,
        Class<K> keyClass,
        Class<V> valueClass,
        Map<K, I> map,
        UnaryOperator<K> toKeyNamespace,
        UnaryOperator<K> fromKeyNamespace,
        Function<I, V> toValueMapFunc,
        Function<V, @Nullable I> fromValueMapFunc
    ) {
        this(debugNaming, keyClass, valueClass, map, toKeyNamespace, fromKeyNamespace, (K k, I i) -> toValueMapFunc.apply(i), fromValueMapFunc);
    }

    public ImmutableDelegatingMap(
        String debugNaming,
        Class<K> keyClass,
        Class<V> valueClass,
        Map<K, I> map,
        UnaryOperator<K> toKeyNamespace,
        UnaryOperator<K> fromKeyNamespace,
        BiFunction<K, I, V> toValueMapFunc,
        Function<V, @Nullable I> fromValueMapFunc
    ) {
        this.debugNaming = debugNaming;

        this.keyClass = keyClass;
        this.valueClass = valueClass;

        this.map = map;

        this.toKeyNamespace = toKeyNamespace;
        this.fromKeyNamespace = fromKeyNamespace;

        this.toValueMapFunc = toValueMapFunc;
        this.fromValueMapFunc = fromValueMapFunc;
    }

    public ImmutableDelegatingMap<K, V, I> errorMessageSupplier(Runnable errorMessage) {
        this.errorMessage = errorMessage;

        return this;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if(!(keyClass.isInstance(key))) return false;

        return this.map.containsKey(this.fromKeyNamespace.apply((K) key));
    }

    @Override
    public boolean containsValue(Object value) {
        if(!(valueClass.isInstance(value))) return false;

        var possibleValue = this.fromValueMapFunc.apply((V) value);

        return possibleValue != null && this.map.containsValue(possibleValue);
    }

    @Override
    @Nullable
    public V get(Object key) {
        if(!(keyClass.isInstance(key))) return null;

        var convertedKey = this.fromKeyNamespace.apply((K) key);

        var entry = this.map.get(convertedKey);

        if(entry == null) {
//            LOGGER.error("[ImmutableDelegatingMap - {}]: Unable to get the desired entry from the given key [{}] as the converted value [{}] was not found!", this.debugNaming, key, convertedKey);
//            LOGGER.error("[ImmutableDelegatingMap - {}]: Dumping entire map: {}", this.debugNaming, this.map);
//
//            errorMessage.run();

            return null;
        }

        return this.toValueMapFunc.apply((K) key, entry);
    }

    @Override
    public @NotNull Set<K> keySet() {
        return new ImmutableWrappingCollection<>(
            this.map.keySet(),
            toKeyNamespace,
            (ks, object) -> ks.contains(fromKeyNamespace.apply(object))
        );
    }

    @Override
    public @NotNull Collection<V> values() {
        return new ImmutableWrappingCollection<>(
            this.map.entrySet(),
            kiEntry -> this.toValueMapFunc.apply(kiEntry.getKey(), kiEntry.getValue()),
            (entries, v) -> this.map.containsValue(this.fromValueMapFunc.apply(v))
        );
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new ImmutableWrappingCollection<>(
            this.map.entrySet(),
            kiEntry -> Map.entry(
                this.toKeyNamespace.apply(kiEntry.getKey()),
                this.toValueMapFunc.apply(kiEntry.getKey(), kiEntry.getValue())),
            (entries, kvEntry) -> {
                var entry = this.map.get(this.fromKeyNamespace.apply(kvEntry.getKey()));

                return Objects.equals(fromValueMapFunc.apply(kvEntry.getValue()), entry);
            }
        );
    }

    //--

    @Override public @Nullable V put(K key, V value) { return null; }
    @Override public V remove(Object key) { return null; }
    @Override public void putAll(@NotNull Map<? extends K, ? extends V> m) {}
    @Override public void clear() {}
}

