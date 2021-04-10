package com.binarystore.meta;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface MetadataStore {
    void put(int key, int version, @Nonnull byte[] data);

    @CheckForNull
    byte[] get(int key, int version);

    boolean contains(int key, int version);
}
