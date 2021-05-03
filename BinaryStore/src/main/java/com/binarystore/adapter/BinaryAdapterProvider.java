package com.binarystore.adapter;

import com.binarystore.dependency.Dependencies;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface BinaryAdapterProvider {
    @CheckForNull
    <T> BinaryAdapter<T> getAdapterForClass(
            @Nonnull Class<T> clazz,
            @CheckForNull Dependencies dependencies
    ) throws Exception;

    @CheckForNull
    BinaryAdapter<?> getAdapterByKey(
            @Nonnull Key<?> key,
            @CheckForNull Dependencies dependencies
    ) throws Exception;

    @CheckForNull
    <B extends BinaryAdapter<?>> B getAdapterByClass(
            @Nonnull Class<B> clazz,
            @CheckForNull Dependencies dependencies
    ) throws Exception;

    <T, B extends BinaryAdapter<T>> B createAdapter(
            @Nonnull AdapterFactory<T, B> factory,
            @CheckForNull Dependencies dependencies
    ) throws Exception;
}
