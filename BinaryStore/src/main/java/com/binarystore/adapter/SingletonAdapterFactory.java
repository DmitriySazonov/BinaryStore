package com.binarystore.adapter;

import javax.annotation.Nonnull;

public final class SingletonAdapterFactory<T, A extends BinaryAdapter<T>> implements AdapterFactory<T, A> {

    private final Key<?> adapterKey;
    private final A adapter;

    public SingletonAdapterFactory(Key<?> adapterKey, A adapter) {
        this.adapterKey = adapterKey;
        this.adapter = adapter;
    }

    @Nonnull
    @Override
    public A create(@Nonnull Context context) {
        return adapter;
    }

    @Override
    public Key<?> adapterKey() {
        return adapterKey;
    }
}
