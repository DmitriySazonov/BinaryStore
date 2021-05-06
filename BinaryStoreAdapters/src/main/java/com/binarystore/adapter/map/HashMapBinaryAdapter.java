package com.binarystore.adapter.map;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.util.HashMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class HashMapBinaryAdapter extends AbstractMapBinaryAdapter<HashMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.HASH_MAP;

    protected HashMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) {
        super(provider, settings);
    }

    @Nonnull
    @Override
    protected HashMap<?, ?> createMap(int size, @Nonnull ByteBuffer buffer) {
        return new HashMap<>(size, 1f);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends MapFactory<HashMap, HashMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return HashMapBinaryAdapter.KEY;
        }

        @Override
        protected HashMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) {
            return new HashMapBinaryAdapter(provider, settings);
        }
    }
}
