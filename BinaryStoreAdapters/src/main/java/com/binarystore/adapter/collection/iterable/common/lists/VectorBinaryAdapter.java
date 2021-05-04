package com.binarystore.adapter.collection.iterable.common.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

import java.util.Vector;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class VectorBinaryAdapter extends CollectionBinaryAdapter<Vector> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.VECTOR;

    protected VectorBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected Vector<?> createCollection(int size) {
        return new Vector<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<Vector, VectorBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public VectorBinaryAdapter create(@Nonnull Context context) {
            return new VectorBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}
