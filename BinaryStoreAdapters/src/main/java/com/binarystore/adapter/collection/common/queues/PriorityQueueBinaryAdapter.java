package com.binarystore.adapter.collection.common.queues;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;

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

    private static class Factory extends CollectionFactory<PriorityQueue, PriorityQueueBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public PriorityQueueBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new PriorityQueueBinaryAdapter(provider, settings);
        }
    }

}

