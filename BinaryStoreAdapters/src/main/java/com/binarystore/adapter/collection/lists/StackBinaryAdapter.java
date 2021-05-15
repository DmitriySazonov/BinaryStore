package com.binarystore.adapter.collection.lists;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.Stack;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class StackBinaryAdapter extends AbstractCollectionBinaryAdapter<Stack> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.STACK;

    protected StackBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected Stack<?> createCollection(int size) {
        return new Stack<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<Stack, StackBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public StackBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new StackBinaryAdapter(provider, settings);
        }
    }

}
