package com.binarystore.dependency;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface Dependencies {
    @CheckForNull
    <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name);
}
