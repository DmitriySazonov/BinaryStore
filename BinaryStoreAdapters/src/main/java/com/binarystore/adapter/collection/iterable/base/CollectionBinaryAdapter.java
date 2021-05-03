package com.binarystore.adapter.collection.iterable.base;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.iterable.settings.CollectionSettings;
import com.binarystore.buffer.ByteBuffer;
import java.util.Collection;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public abstract class CollectionBinaryAdapter<T extends Collection> extends IterableBinaryAdapter<T> {

    protected CollectionBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @CheckForNull final CollectionSettings settings
    ) {
        super(provider, settings);
    }

    @Override
    int getCollectionSize(T collection) {
        return collection.size();
    }

    protected abstract T createCollection(int size);

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Adapters adapters = new Adapters();
        final int size = byteBuffer.readInt();
        final int startDataOffset = byteBuffer.readInt();
        final T collection = createCollection(size);
        final Collection<Object> mutableCollection = (Collection<Object>) collection;
        byteBuffer.setOffset(startDataOffset);
        for (int i = 0; i < size; i++) {
            adapters.setValueClass(Key.read(byteBuffer));
            mutableCollection.add(adapters.lastValueAdapter.deserialize(byteBuffer));
        }
        return collection;
    }
}
