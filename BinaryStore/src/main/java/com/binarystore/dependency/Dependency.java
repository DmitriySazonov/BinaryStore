package com.binarystore.dependency;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface Dependency<T> {

    @CheckForNull
    String name();

    @Nonnull
    Class<T> typeClass();

    @CheckForNull
    T provide();
}
