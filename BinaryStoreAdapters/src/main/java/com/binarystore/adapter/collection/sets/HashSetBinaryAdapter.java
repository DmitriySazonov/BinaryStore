package com.binarystore.adapter.collection.sets;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.HashSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class HashSetBinaryAdapter extends AbstractCollectionBinaryAdapter<HashSet> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.HASH_SET;

    protected HashSetBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected HashSet<?> createCollection(int size) {
        return new HashSet<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<HashSet, HashSetBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public HashSetBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new HashSetBinaryAdapter(provider, settings);
        }
    }

}
