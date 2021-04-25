package com.binarystore.adapter.collection.map;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;

import java.util.HashMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class HashMapBinaryAdapter extends AbstractMapBinaryAdapter<HashMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.HASH_MAP;

    protected HashMapBinaryAdapter(BinaryAdapterProvider provider) {
        super(provider);
    }

    @Override
    protected HashMap<?, ?> createMap(int size) {
        return new HashMap<>(size, 1f);
    }

    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<HashMap, HashMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return HashMapBinaryAdapter.KEY;
        }

        @Nonnull
        @Override
        public HashMapBinaryAdapter create(@Nonnull Context context) {
            return new HashMapBinaryAdapter(context.provider);
        }
    }
}
