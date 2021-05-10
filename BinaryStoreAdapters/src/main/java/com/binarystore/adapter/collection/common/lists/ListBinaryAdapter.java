package com.binarystore.adapter.collection.common.lists;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.dependency.Properties;
import com.binarystore.dependency.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

public class ListBinaryAdapter extends CollectionBinaryAdapter<List> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.LIST;

    @Nonnull
    private final BinaryAdapterProvider provider;
    @Nonnull
    private final Properties properties;

    protected ListBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings,
            @Nonnull Properties properties
    ) {
        super(provider, settings);
        this.provider = provider;
        this.properties = properties;
    }


    @Override
    @SuppressWarnings("unchecked")
    public int getSize(@Nonnull List value) throws Exception {
        final BinaryAdapter<List> adapter =
                (BinaryAdapter<List>) provider.getAdapterForClass(value.getClass(), properties);
        if (adapter != null) {
            return adapter.key().getSize() + adapter.getSize(value);
        }
        return key().getSize() + super.getSize(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull List value) throws Exception {
        final BinaryAdapter<List> adapter =
                (BinaryAdapter<List>) provider.getAdapterForClass(value.getClass(), properties);
        if (adapter != null) {
            adapter.key().saveTo(byteBuffer);
            adapter.serialize(byteBuffer, value);
        } else {
            key().saveTo(byteBuffer);
            super.serialize(byteBuffer, value);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public List deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Key<?> key = Key.read(byteBuffer);
        if (KEY.equals(key)) {
            return super.deserialize(byteBuffer);
        } else {
            final BinaryAdapter<List> adapter =
                    (BinaryAdapter<List>) provider.getAdapterByKey(key, properties);
            Objects.requireNonNull(adapter);
            return adapter.deserialize(byteBuffer);
        }
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
