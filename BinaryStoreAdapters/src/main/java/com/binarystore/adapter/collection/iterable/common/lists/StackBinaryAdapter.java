package com.binarystore.adapter.collection.iterable.common.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

import java.util.Stack;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class StackBinaryAdapter extends CollectionBinaryAdapter<Stack> {

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

    private static class Factory implements AdapterFactory<Stack, StackBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public StackBinaryAdapter create(@Nonnull Context context) {
            return new StackBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
