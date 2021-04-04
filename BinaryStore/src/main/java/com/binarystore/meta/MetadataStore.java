package com.binarystore.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MetadataStore {
    void put(int key, int version, @NotNull byte[] data);
    @Nullable byte[] get(int key, int version);
    boolean contains(int key, int version);
}
