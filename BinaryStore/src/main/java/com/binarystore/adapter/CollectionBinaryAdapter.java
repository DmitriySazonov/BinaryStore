package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public interface CollectionBinaryAdapter<T> extends BinaryAdapter<T> {

    enum UnknownItemStrategy {
        SKIP_ITEM, THROW_EXCEPTION
    }

    class Settings {

        final UnknownItemStrategy unknownItemStrategy;

        Settings(@Nonnull final UnknownItemStrategy unknownItemStrategy) {
            this.unknownItemStrategy = unknownItemStrategy;
        }
    }


    int getSize(@Nonnull T value, @Nonnull Settings settings) throws Exception;

    void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value, @Nonnull Settings settings) throws Exception;

    @Nonnull
    T deserialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Settings settings) throws Exception;

    @Nonnull
    Settings getDefaultSettings();
}
