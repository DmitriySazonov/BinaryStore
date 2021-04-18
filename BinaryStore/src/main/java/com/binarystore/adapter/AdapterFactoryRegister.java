package com.binarystore.adapter;


import javax.annotation.Nonnull;

public interface AdapterFactoryRegister {

    <T> void register(@Nonnull Class<T> clazz, @Nonnull AdapterFactory<T, ? extends BinaryAdapter<T>> factory);
}
