package com.binarystore.map;

import com.binarystore.adapter.LazyBinaryEntry;

import java.util.Map;

public class LazyEntry<K, V> implements Map.Entry<K, V> {

    private final K key;
    private V value;

    LazyEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

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
            setValue(value);
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