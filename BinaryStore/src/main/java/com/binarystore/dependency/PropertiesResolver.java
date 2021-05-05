package com.binarystore.dependency;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class PropertiesResolver implements Properties {

    private final HashMap<Class<?>, Property<?>> classDependencyMap = new HashMap<>();

    public void addProperty(Property<?> property) {
        classDependencyMap.put(property.typeClass(), property);
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        Property<?> property = classDependencyMap.get(tClass);
        return (T) (property != null && Objects.equals(property.name(), name)
                ? property.provide() : null);
    }
}
