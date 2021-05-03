package com.binarystore.dependency;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class EmptyDependencies implements Dependencies {

    public static final EmptyDependencies instance = new EmptyDependencies();

    private EmptyDependencies() {

    }

    @CheckForNull
    @Override
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        return null;
    }
}
