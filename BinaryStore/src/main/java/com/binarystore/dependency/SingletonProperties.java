package com.binarystore.dependency;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class SingletonProperties implements Properties {

    @Nonnull
    private final Property<?> property;

    public SingletonProperties(@Nonnull final Property<?> property) {
        this.property = property;
    }

    @CheckForNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        return (T) (property.typeClass() == tClass
                && Objects.equals(property.name(), name) ? property.provide() : null);
    }
}
