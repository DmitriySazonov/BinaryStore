package com.binarystore.map;

import com.binarystore.adapter.LazyBinaryEntry;
import com.binarystore.collections.SimpleBinaryLazyList;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;

public class BinaryLazyMap<K, V> implements Map<K, V> {

    @Nonnull
    protected HashMap<K, Object> innerMap;

    public BinaryLazyMap(
            @Nonnull Map<K, Object> map
    ) {
        innerMap = new HashMap<>(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Object value = innerMap.get(key);
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<V>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            innerMap.put((K) key, value);
        }
        return (V) value;
    }

    @Nonnull
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<Entry<K, V>> entrySet() {
        Set result = new HashSet(innerMap.size());
        for (Entry<K, Object> entry : innerMap.entrySet()) {
            if (entry.getValue() instanceof LazyBinaryEntry) {
                result.add(new LazyEntry<>(entry.getKey(), entry.getValue()));
            } else {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public int size() {
        return innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return innerMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Set<K> keySet() {
        return innerMap.keySet();
    }

    @Nonnull
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Collection<V> values() {
        return new SimpleBinaryLazyList(innerMap.values());
    }

    @Override
    public V getOrDefault(Object o, V v) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> biConsumer) {

    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction) {

    }

    @Override
    public V putIfAbsent(K k, V v) {
        return null;
    }

    @Override
    public boolean remove(Object o, Object o1) {
        return false;
    }

    @Override
    public boolean replace(K k, V v, V v1) {
        return false;
    }

    @Override
    public V replace(K k, V v) {
        return null;
    }

    @Override
    public V computeIfAbsent(K k, @Nonnull Function<? super K, ? extends V> function) {
        return null;
    }

    @Override
    public V computeIfPresent(K k, @Nonnull BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return null;
    }

    @Override
    public V compute(K k, @Nonnull BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return null;
    }

    @Override
    public V merge(K k, @Nonnull V v, @Nonnull BiFunction<? super V, ? super V, ? extends V> biFunction) {
        return null;
    }
}
