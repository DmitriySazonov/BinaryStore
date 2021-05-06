package com.binarystore.adapter.map;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.dependency.PropertiesUtils;

import java.util.Map;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
abstract class MapFactory<M extends Map, A extends BinaryAdapter<M>> implements AdapterFactory<M, A> {

    protected abstract A create(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) throws Exception;

    @Nonnull
    @Override
    public final A create(@Nonnull Context context) throws Exception {
        MapSettings settings = PropertiesUtils.getOrDefault(context, MapSettings.class,
                MapSettings.defaultSettings);
        return create(context.getAdapterProvider(), settings);
    }
}
