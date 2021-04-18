package com.binarystore.adapter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface BinaryAdapterProvider {
    @CheckForNull
    <T> BinaryAdapter<T> getAdapterForClass(@Nonnull Class<T> clazz) throws Exception;

    @CheckForNull
    BinaryAdapter<?> getAdapterByKey(Key<?> key) throws Exception;

    @CheckForNull
    <B extends BinaryAdapter<?>> B getAdapterByClass(Class<B> clazz) throws Exception;

    <T, B extends BinaryAdapter<T>> B createAdapter(AdapterFactory<T, B> factory) throws Exception;
}
