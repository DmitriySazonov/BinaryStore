package com.binarystore.adapter.collection.common.queues;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.ArrayDeque;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class ArrayDequeBinaryAdapter extends CollectionBinaryAdapter<ArrayDeque> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ARRAY_DEQUE;

    protected ArrayDequeBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected ArrayDeque<?> createCollection(int size) {
        return new ArrayDeque<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<ArrayDeque, ArrayDequeBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public ArrayDequeBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new ArrayDequeBinaryAdapter(provider, settings);
        }
    }

}
