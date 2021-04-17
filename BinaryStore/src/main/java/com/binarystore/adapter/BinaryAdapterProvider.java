package com.binarystore.adapter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface BinaryAdapterProvider {
    @CheckForNull
    <T> BinaryAdapter<T> getAdapter(@Nonnull Class<T> clazz);

    @CheckForNull
    BinaryAdapter<?> getAdapter(Key<?> id);
}
