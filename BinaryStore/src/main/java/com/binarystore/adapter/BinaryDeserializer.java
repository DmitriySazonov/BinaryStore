package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public interface BinaryDeserializer<T> {

    @Nonnull
    T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception;
}
