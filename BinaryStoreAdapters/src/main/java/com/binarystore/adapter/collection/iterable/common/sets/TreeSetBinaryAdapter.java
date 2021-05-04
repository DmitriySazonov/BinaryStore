package com.binarystore.adapter.collection.iterable.common.sets;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class TreeSetBinaryAdapter extends CollectionBinaryAdapter<TreeSet> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.TREE_SET;

    protected TreeSetBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected TreeSet<?> createCollection(int size) {
        return new TreeSet<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<TreeSet, TreeSetBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public TreeSetBinaryAdapter create(@Nonnull Context context) {
            return new TreeSetBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}

