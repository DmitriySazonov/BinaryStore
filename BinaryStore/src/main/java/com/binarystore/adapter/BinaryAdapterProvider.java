package com.binarystore.adapter;

public interface BinaryAdapterProvider {
    <T> BinaryAdapter<T> getAdapter(Class<T> clazz);
    BinaryAdapter<?> getAdapter(int id);
}
