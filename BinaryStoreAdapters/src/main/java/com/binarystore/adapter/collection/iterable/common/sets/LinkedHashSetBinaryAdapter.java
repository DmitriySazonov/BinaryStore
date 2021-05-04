package com.binarystore.adapter.collection.iterable.common.sets;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

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

    private static class Factory implements AdapterFactory<LinkedHashSet, LinkedHashSetBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public LinkedHashSetBinaryAdapter create(@Nonnull Context context) {
            return new LinkedHashSetBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
