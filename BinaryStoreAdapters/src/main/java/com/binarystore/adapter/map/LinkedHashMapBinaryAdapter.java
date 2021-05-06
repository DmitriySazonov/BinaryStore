package com.binarystore.adapter.map;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class LinkedHashMapBinaryAdapter extends AbstractMapBinaryAdapter<LinkedHashMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.LINKED_HASH_MAP;

    protected LinkedHashMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) {
        super(provider, settings);
    }

    @Nonnull
    @Override
    protected LinkedHashMap createMap(int size, @Nonnull ByteBuffer buffer) {
        return new LinkedHashMap(size, 1f);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends MapFactory<LinkedHashMap, LinkedHashMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected LinkedHashMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) {
            return new LinkedHashMapBinaryAdapter(provider, settings);
        }
    }
}
