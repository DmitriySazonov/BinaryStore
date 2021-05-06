package com.binarystore.manager;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.dependency.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class AdapterFactoryContext implements AdapterFactory.Context {

    @CheckForNull
    private final AdapterFactoryContext rootContext;
    @Nonnull
    private final BinaryAdapterProvider provider;
    @CheckForNull
    private final Properties optionalProperties;

    public AdapterFactoryContext(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull Properties properties
    ) {
        this(provider, null, properties);
    }

    private AdapterFactoryContext(
            @Nonnull BinaryAdapterProvider provider,
            @CheckForNull AdapterFactoryContext rootContext,
            @CheckForNull Properties properties
    ) {
        this.provider = provider;
        this.rootContext = rootContext;
        this.optionalProperties = properties;
    }

    public AdapterFactoryContext wrap(@Nonnull Properties properties) {
        return new AdapterFactoryContext(provider, this, properties);
    }

    @CheckForNull
    @Override
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        if (optionalProperties == null) {
            return null;
        }
        T property = optionalProperties.get(tClass, name);
        if (property == null && rootContext != null) {
            property = rootContext.get(tClass, name);
        }
        return property;
    }

    @Override
    public BinaryAdapterProvider getAdapterProvider() {
        return provider;
    }
}
