package com.binarystore.adapter;

import org.jetbrains.annotations.NotNull;

public interface AdapterFactoryRegister {

    <T> void register(@NotNull Class<T> clazz, @NotNull AdapterFactory<T> factory);
}
