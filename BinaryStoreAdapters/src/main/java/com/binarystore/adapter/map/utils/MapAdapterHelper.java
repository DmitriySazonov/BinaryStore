package com.binarystore.adapter.map.utils;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.collection.utils.CollectionAdapterUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class MapAdapterHelper {

    private Key<?> lastKeyKey = null;
    private Class<?> lastKeyClass = null;
    public BinaryAdapter<Object> lastKeyAdapter = null;

    private Key<?> lastValueKey = null;
    private Class<?> lastValueClass = null;
    public BinaryAdapter<Object> lastValueAdapter = null;

    private final BinaryAdapterProvider adapterProvider;
    private final boolean allowUseValueAsAdapter;

    public MapAdapterHelper(
            @Nonnull BinaryAdapterProvider adapterProvider
    ) {
        this(adapterProvider, false);
    }

    public MapAdapterHelper(
            @Nonnull BinaryAdapterProvider adapterProvider,
            boolean allowUseValueAsAdapter
    ) {
        this.adapterProvider = adapterProvider;
        this.allowUseValueAsAdapter = allowUseValueAsAdapter;
    }

    @SuppressWarnings("unchecked")
    public final void setKeyClass(@CheckForNull Object value) throws Exception {
        if (allowUseValueAsAdapter && value instanceof BinaryAdapter) {
            lastKeyClass = value.getClass();
            lastKeyAdapter = (BinaryAdapter<Object>) value;
            return;
        }

        final Class<?> valueClass = value != null ? value.getClass() : NullBinaryAdapter.NULL_CLASS;
        if (valueClass != lastKeyClass) {
            lastKeyClass = valueClass;
            lastKeyAdapter = CollectionAdapterUtils.getAdapterForClass(adapterProvider, valueClass);
        }
    }

    public final void setKeyKey(Key<?> valueClass) throws Exception {
        if (!valueClass.equals(lastKeyKey)) {
            lastKeyKey = valueClass;
            lastKeyAdapter = CollectionAdapterUtils.getAdapterForKey(adapterProvider, valueClass);
        }
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
