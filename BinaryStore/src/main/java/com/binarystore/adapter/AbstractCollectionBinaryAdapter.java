package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public abstract class AbstractCollectionBinaryAdapter<T> implements CollectionBinaryAdapter<T> {

    @Nonnull
    private final Settings defaultSettings;

    public AbstractCollectionBinaryAdapter() {
        this(new Settings(UnknownItemStrategy.THROW_EXCEPTION));
    }

    public AbstractCollectionBinaryAdapter(@Nonnull final Settings settings) {
        this.defaultSettings = settings;
    }

    @Override
    public int getSize(@Nonnull T value) throws Exception {
        return getSize(value, defaultSettings);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        serialize(byteBuffer, value, defaultSettings);
    }

    @Nonnull
    @Override
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return deserialize(byteBuffer, defaultSettings);
    }

    @Nonnull
    @Override
    public Settings getDefaultSettings() {
        return defaultSettings;
    }
}
