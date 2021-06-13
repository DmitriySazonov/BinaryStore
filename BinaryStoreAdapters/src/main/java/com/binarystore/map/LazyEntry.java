package com.binarystore.map;

import com.binarystore.adapter.LazyBinaryEntry;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LazyEntry<K, V> implements Map.Entry<K, V> {

    @Nonnull
    private final K key;
    @Nullable
    private V value;

    LazyEntry(@Nonnull K key, @Nullable V value) {
        this.key = key;
        this.value = value;
    }

    @Nonnull
    @Override
    public K getKey() {
        return key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getValue() {
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<V>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    @Override
    public V setValue(V v) {
        V bufValue = this.value;
        this.value = v;
        return bufValue;
    }
}
