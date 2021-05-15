package com.binarystore.adapter.collection.sets;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.dependency.Properties;
import com.binarystore.dependency.PropertiesUtils;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class SetBinaryAdapter extends CollectionBinaryAdapter<Set> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.SET;

    protected SetBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings,
            @Nonnull Properties properties
    ) {
        super(provider, settings, properties);
    }

    @Override
    protected Set<?> createCollection(int size) {
        return new HashSet<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<Set, SetBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }


        @Nonnull
        @Override
        public SetBinaryAdapter create(@Nonnull Context context) {
            CollectionSettings settings = PropertiesUtils.getOrDefault(context, CollectionSettings.class,
                    CollectionSettings.defaultSettings);
            return new SetBinaryAdapter(context.getAdapterProvider(), settings, context);
        }
    }

}