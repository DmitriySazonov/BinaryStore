package com.binarystore.adapter.collection;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.dependency.PropertiesUtils;

import java.util.Collection;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class CollectionFactory<M extends Collection, A extends BinaryAdapter<M>> implements AdapterFactory<M, A> {

    protected abstract A create(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull CollectionSettings settings
    ) throws Exception;

    @Nonnull
    @Override
    public final A create(@Nonnull Context context) throws Exception {
        CollectionSettings settings = PropertiesUtils.getOrDefault(context, CollectionSettings.class,
                CollectionSettings.defaultSettings);
        return create(context.getAdapterProvider(), settings);
    }
}

