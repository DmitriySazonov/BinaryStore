package com.binarystore.collections;

import com.binarystore.adapter.LazyBinaryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

public final class SimpleBinaryLazyList<T> extends AbstractBinaryLazyList<T> {

    private final ArrayList<Object> innerList;

    public SimpleBinaryLazyList(
            @Nonnull Collection<T> values
    ) {
        this(values.toArray());
    }

    public SimpleBinaryLazyList(
            LazyBinaryEntry<T>[] entries
    ) {
        this((Object[]) entries);
    }

    private SimpleBinaryLazyList(
            @Nonnull Object[] values
    ) {
        innerList = new ArrayList<>(Arrays.asList(values));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        Object value = innerList.get(index);
        if (value instanceof LazyBinaryEntry) {
            try {
                value = ((LazyBinaryEntry<T>) value).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            innerList.set(index, value);
        }
        return (T) value;
    }

    @Override
    public int size() {
        return innerList.size();
    }
}