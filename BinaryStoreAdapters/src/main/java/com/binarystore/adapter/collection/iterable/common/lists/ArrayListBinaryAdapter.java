package com.binarystore.adapter.collection.iterable.common.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;
import java.util.ArrayList;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class ArrayListBinaryAdapter extends CollectionBinaryAdapter<ArrayList> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ARRAY_LIST;

    protected ArrayListBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
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

    private static class Factory implements AdapterFactory<ArrayList, ArrayListBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return ArrayListBinaryAdapter.KEY;
        }

        @Nonnull
        @Override
        public ArrayListBinaryAdapter create(@Nonnull Context context) {
            return new ArrayListBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
