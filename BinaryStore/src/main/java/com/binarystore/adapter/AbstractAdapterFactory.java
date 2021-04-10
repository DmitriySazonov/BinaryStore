package com.binarystore.adapter;

public abstract class AbstractAdapterFactory<T> implements AdapterFactory<T> {

    private final int adapterId;

    public AbstractAdapterFactory(int adapterId) {
        this.adapterId = adapterId;
    }

    @Override
    public int adapterId() {
        return adapterId;
    }
}
