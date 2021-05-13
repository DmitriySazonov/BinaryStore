package com.binarystore.adapter.collection.common.queues;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.AbstractCollectionAdapter;
import com.binarystore.dependency.Properties;
import com.binarystore.dependency.PropertiesUtils;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class QueueBinaryAdapter extends AbstractCollectionAdapter<Queue> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.QUEUE;

    protected QueueBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings,
            @Nonnull Properties properties
    ) {
        super(provider, settings, properties);
    }

    @Override
    protected Queue<?> createCollection(int size) {
        return new ArrayDeque<>(size);
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<Queue, QueueBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }


        @Nonnull
        @Override
        public QueueBinaryAdapter create(@Nonnull Context context) {
            CollectionSettings settings = PropertiesUtils.getOrDefault(context, CollectionSettings.class,
                    CollectionSettings.defaultSettings);
            return new QueueBinaryAdapter(context.getAdapterProvider(), settings, context);
        }
    }
}