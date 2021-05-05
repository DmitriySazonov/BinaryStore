package com.binarystore.adapter;

import com.binarystore.dependency.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface BinaryAdapterProvider {
    @CheckForNull
    <T> BinaryAdapter<T> getAdapterForClass(
            @Nonnull Class<T> clazz,
            @CheckForNull Properties properties
    ) throws Exception;

    @CheckForNull
    BinaryAdapter<?> getAdapterByKey(
            @Nonnull Key<?> key,
            @CheckForNull Properties properties
    ) throws Exception;

    @CheckForNull
    <B extends BinaryAdapter<?>> B getAdapterByClass(
            @Nonnull Class<B> clazz,
            @CheckForNull Properties properties
    ) throws Exception;

    <T, B extends BinaryAdapter<T>> B createAdapter(
            @Nonnull AdapterFactory<T, B> factory,
            @CheckForNull Properties properties
    ) throws Exception;
}
