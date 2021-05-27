package com.binarystore.map;

import com.binarystore.adapter.LazyBinaryEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class SimpleBinaryLazyMap<K, V> extends AbstractBinaryLazyMap<K, V> {

    private final HashMap<Object, Object> innerMap;

    public SimpleBinaryLazyMap(
            @Nonnull Map<K, V> map
    ) {
        innerMap = new HashMap<>();
        innerMap.putAll(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object o) {
        Object value = innerMap.get(o);
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<V>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            innerMap.put(o, value);
        }
        return (V) value;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>((Collection<? extends Entry<K, V>>) innerMap.entrySet());
    }
}
