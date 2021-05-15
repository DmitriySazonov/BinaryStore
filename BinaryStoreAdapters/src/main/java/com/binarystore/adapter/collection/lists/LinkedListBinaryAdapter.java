package com.binarystore.adapter.collection.lists;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

import java.util.LinkedList;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class LinkedListBinaryAdapter extends AbstractCollectionBinaryAdapter<LinkedList> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.LINKED_LIST;

    protected LinkedListBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected LinkedList<?> createCollection(int size) {
        return new LinkedList<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends CollectionFactory<LinkedList, LinkedListBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public LinkedListBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new LinkedListBinaryAdapter(provider, settings);
        }
    }

}
