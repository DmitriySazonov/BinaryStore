package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public interface BinarySerializer<T> {

    int getSize(@Nonnull T value) throws Exception;

    void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception;
}
