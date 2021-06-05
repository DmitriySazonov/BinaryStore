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

public class InnerLazyMap<K, V> implements Map<K, V> {

    protected HashMap<K, Object> innerHashMap = new HashMap<K, Object>();

    @Override
    public int size() {
        return innerHashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerHashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return innerHashMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        //TODO unsupported now
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object o) {
        Object value = innerHashMap.get(o);
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<V>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            innerHashMap.put((K) o, value);
        }
        return (V) value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(K k, V v) {
        return (V) innerHashMap.put(k, v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object o) {
        return (V) innerHashMap.remove(0);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        innerHashMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return innerHashMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return new SimpleBinaryLazyList(innerHashMap.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = new HashSet<>(innerHashMap.size());
        for (Entry<K, Object> entry : innerHashMap.entrySet()) {
            result.add(new LazyEntry<>(entry.getKey(), (V) entry.getValue()));
        }
        return result;
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
    public V computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        return null;
    }

    @Override
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return null;
    }

    @Override
    public V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return null;
    }

    @Override
    public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction) {
        return null;
    }

}
