package com.binarystore.adapter.collection.map;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.util.concurrent.ConcurrentSkipListMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class ConcurrentSkipListMapBinaryAdapter extends AbstractMapBinaryAdapter<ConcurrentSkipListMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.CONCURRENT_SKI_LIST_MAP;

    protected ConcurrentSkipListMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) {
        super(provider, settings);
    }

    @Nonnull
    @Override
    protected ConcurrentSkipListMap createMap(int size, @Nonnull ByteBuffer buffer) {
        return new ConcurrentSkipListMap();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends MapFactory<ConcurrentSkipListMap, ConcurrentSkipListMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected ConcurrentSkipListMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) {
            return new ConcurrentSkipListMapBinaryAdapter(provider, settings);
        }
    }
}
