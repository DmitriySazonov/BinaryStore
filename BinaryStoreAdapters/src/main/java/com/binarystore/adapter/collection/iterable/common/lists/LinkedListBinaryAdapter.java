package com.binarystore.adapter.collection.iterable.common.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;
import java.util.LinkedList;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class LinkedListBinaryAdapter extends CollectionBinaryAdapter<LinkedList> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ARRAY_LIST;

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

    private static class Factory implements AdapterFactory<LinkedList, LinkedListBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public LinkedListBinaryAdapter create(@Nonnull Context context) {
            return new LinkedListBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
