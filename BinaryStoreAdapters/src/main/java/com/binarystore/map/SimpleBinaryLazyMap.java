package com.binarystore.map;

import com.binarystore.adapter.LazyBinaryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class SimpleBinaryLazyMap<K, V> extends AbstractBinaryLazyMap<K, V> {

    private final HashMap<K, V> innerMap;

    public SimpleBinaryLazyMap(
            @Nonnull Map<K, V> map
    ) {
        innerMap = new HashMap<>();
        innerMap.putAll(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object o) {
        V value = innerMap.get(o);
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<V>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            innerMap.put((K) o, value);
        }
        return value;
    }

    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return innerMap.entrySet();
    }
}
