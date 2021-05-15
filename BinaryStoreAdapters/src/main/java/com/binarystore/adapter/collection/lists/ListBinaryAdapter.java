package com.binarystore.adapter.collection.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.dependency.Properties;
import com.binarystore.dependency.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class ListBinaryAdapter extends CollectionBinaryAdapter<List> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.LIST;

    protected ListBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings,
            @Nonnull Properties properties
    ) {
        super(provider, settings, properties);
    }

    @Override
    protected List<?> createCollection(int size) {
        return new ArrayList<>(size);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<List, ListBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }


        @Nonnull
        @Override
        public ListBinaryAdapter create(@Nonnull Context context) {
            CollectionSettings settings = PropertiesUtils.getOrDefault(context, CollectionSettings.class,
                    CollectionSettings.defaultSettings);
            return new ListBinaryAdapter(context.getAdapterProvider(), settings, context);
        }
    }
}
