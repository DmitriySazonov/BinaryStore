package com.binarystore.adapter.collection.common;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.collection.CollectionBinaryAdapter;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.dependency.Properties;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class AbstractCollectionAdapter<T extends Collection> extends CollectionBinaryAdapter<T> {

    @Nonnull
    private final BinaryAdapterProvider provider;
    @Nonnull
    private final Properties properties;

    protected AbstractCollectionAdapter(
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
    public int getSize(@Nonnull T value) throws Exception {
        final BinaryAdapter<T> adapter =
                (BinaryAdapter<T>) provider.getAdapterForClass(value.getClass(), properties);
        if (adapter != null) {
            return adapter.key().getSize() + adapter.getSize(value);
        }
        return key().getSize() + super.getSize(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        final BinaryAdapter<T> adapter =
                (BinaryAdapter<T>) provider.getAdapterForClass(value.getClass(), properties);
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
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Key<?> key = Key.read(byteBuffer);
        if (key().equals(key)) {
            return super.deserialize(byteBuffer);
        } else {
            final BinaryAdapter<T> adapter =
                    (BinaryAdapter<T>) provider.getAdapterByKey(key, properties);
            Objects.requireNonNull(adapter);
            return adapter.deserialize(byteBuffer);
        }
    }

}

