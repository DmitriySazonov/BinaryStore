package com.binarystore.adapter;

public interface BinaryAdapter<T> {

    int id();

    int getSize(T value) throws Exception;

    void serialize(ByteBuffer byteBuffer, T value) throws Exception;

    T deserialize(ByteBuffer byteBuffer) throws Exception;

    T[] createArray(int size) throws Exception;
}
