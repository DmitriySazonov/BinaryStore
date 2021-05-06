package com.binarystore.adapter;

import com.binarystore.dependency.Properties;

import javax.annotation.Nonnull;

public interface AdapterFactory<T, A extends BinaryAdapter<T>> {

    interface Context extends Properties {
        BinaryAdapterProvider getAdapterProvider();
    }

    Key<?> adapterKey();

    @Nonnull
    A create(@Nonnull Context context) throws Exception;
}
