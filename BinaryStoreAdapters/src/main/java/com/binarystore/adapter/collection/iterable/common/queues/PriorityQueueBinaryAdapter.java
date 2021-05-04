package com.binarystore.adapter.collection.iterable.common.queues;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.base.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;

import java.util.PriorityQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class PriorityQueueBinaryAdapter extends CollectionBinaryAdapter<PriorityQueue> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.PRIORITY_QUEUE;

    protected PriorityQueueBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    protected PriorityQueue<?> createCollection(int size) {
        return new PriorityQueue<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory implements AdapterFactory<PriorityQueue, PriorityQueueBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public PriorityQueueBinaryAdapter create(@Nonnull Context context) {
            return new PriorityQueueBinaryAdapter(context.provider, context.get(CollectionSettings.class, null));
        }
    }

}

