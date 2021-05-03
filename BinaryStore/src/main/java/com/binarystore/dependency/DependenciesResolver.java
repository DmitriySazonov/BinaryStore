package com.binarystore.dependency;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class DependenciesResolver implements Dependencies {

    private final HashMap<Class<?>, Dependency<?>> classDependencyMap = new HashMap<>();

    public void addDependency(Dependency<?> dependency) {
        classDependencyMap.put(dependency.typeClass(), dependency);
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        Dependency<?> dependency = classDependencyMap.get(tClass);
        return (T) (dependency != null && Objects.equals(dependency.name(), name)
                ? dependency.provide() : null);
    }
}
