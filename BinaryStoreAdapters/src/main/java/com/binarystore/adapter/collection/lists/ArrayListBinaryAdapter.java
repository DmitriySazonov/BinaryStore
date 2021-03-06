package com.binarystore.adapter.collection.lists;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.ArrayList;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class ArrayListBinaryAdapter extends AbstractCollectionBinaryAdapter<ArrayList> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ARRAY_LIST;

    protected ArrayListBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected ArrayList<?> createCollection(int size) {
        return new ArrayList<>(size);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<ArrayList, ArrayListBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public ArrayListBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new ArrayListBinaryAdapter(provider, settings);
        }
    }

}
