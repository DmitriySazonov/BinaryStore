package com.binarystore.adapter.collection.common.sets;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.LinkedHashSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class LinkedHashSetBinaryAdapter extends CollectionBinaryAdapter<LinkedHashSet> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.LINKED_HASH_SET;

    protected LinkedHashSetBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected LinkedHashSet<?> createCollection(int size) {
        return new LinkedHashSet<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<LinkedHashSet, LinkedHashSetBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public LinkedHashSetBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new LinkedHashSetBinaryAdapter(provider, settings);
        }
    }

}
