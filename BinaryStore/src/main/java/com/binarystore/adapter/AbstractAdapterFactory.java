package com.binarystore.adapter;

public abstract class AbstractAdapterFactory<T> implements AdapterFactory<T> {

    private final Key<?> adapterKey;

    public AbstractAdapterFactory(Key<?> adapterKey) {
        this.adapterKey = adapterKey;
    }

    @Override
    public Key<?> adapterKey() {
        return adapterKey;
    }
}
