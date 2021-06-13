package com.binarystore.adapter.collection.utils;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class CollectionAdapterHelper {

    public Key<?> lastValueKey = null;
    public Class<?> lastValueClass = null;
    public BinaryAdapter<Object> lastValueAdapter = null;

    private final BinaryAdapterProvider adapterProvider;
    private final boolean allowUseValueAsAdapter;

    public CollectionAdapterHelper(
            @Nonnull BinaryAdapterProvider adapterProvider
    ) {
        this(adapterProvider, false);
    }

    public CollectionAdapterHelper(
            @Nonnull BinaryAdapterProvider adapterProvider,
            boolean allowUseValueAsAdapter
    ) {
        this.adapterProvider = adapterProvider;
        this.allowUseValueAsAdapter = allowUseValueAsAdapter;
    }

    @SuppressWarnings("unchecked")
    public final void setValueClass(@CheckForNull Object value) throws Exception {
        if (allowUseValueAsAdapter && value instanceof BinaryAdapter) {
            lastValueClass = value.getClass();
            lastValueAdapter = (BinaryAdapter<Object>) value;
            return;
        }

        final Class<?> valueClass = value != null ? value.getClass() : NullBinaryAdapter.NULL_CLASS;
        if (valueClass != lastValueClass) {
            lastValueClass = valueClass;
            lastValueAdapter = CollectionAdapterUtils.getAdapterForClass(adapterProvider, valueClass);
        }
    }

    public final void setValueKey(Key<?> valueClass) throws Exception {
        if (!valueClass.equals(lastValueKey)) {
            lastValueKey = valueClass;
            lastValueAdapter = CollectionAdapterUtils.getAdapterForKey(adapterProvider, valueClass);
        }
    }
}
