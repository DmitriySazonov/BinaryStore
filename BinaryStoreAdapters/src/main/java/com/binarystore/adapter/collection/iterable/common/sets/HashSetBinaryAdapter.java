package com.binarystore.adapter.collection.iterable.common.sets;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

import java.util.HashSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class HashSetBinaryAdapter extends CollectionBinaryAdapter<HashSet> {

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

    private static class Factory implements AdapterFactory<HashSet, HashSetBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public HashSetBinaryAdapter create(@Nonnull Context context) {
            return new HashSetBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
