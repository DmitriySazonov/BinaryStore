package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public interface BinaryAdapter<T> {

    @Nonnull
    Key<?> key();

    int getSize(@Nonnull T value) throws Exception;

    void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception;

    @Nonnull
    T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception;
}
