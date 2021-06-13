package com.binarystore.adapter.map;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class ConcurrentHashMapBinaryAdapter extends AbstractMapBinaryAdapter<ConcurrentHashMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.CONCURRENT_HASH_MAP;

    protected ConcurrentHashMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) {
        super(provider, settings);
    }

    @Nonnull
    @Override
    protected ConcurrentHashMap createMap(int size) {
        return new ConcurrentHashMap(size, 1f);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends MapFactory<ConcurrentHashMap, ConcurrentHashMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected ConcurrentHashMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) {
            return new ConcurrentHashMapBinaryAdapter(provider, settings);
        }
    }
}
