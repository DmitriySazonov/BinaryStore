package com.binarystore.dependency;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class EmptyProperties implements Properties {

    public static final EmptyProperties instance = new EmptyProperties();

    private EmptyProperties() {

    }

    @CheckForNull
    @Override
    public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
        return null;
    }
}
