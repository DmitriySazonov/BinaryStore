package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

public interface BinaryAdapter<T> {

    Key id();

    int getSize(T value) throws Exception;

    void serialize(ByteBuffer byteBuffer, T value) throws Exception;

    T deserialize(ByteBuffer byteBuffer) throws Exception;
}
