package com.binarystore.meta;

import java.util.HashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class MetadataStoreInMemory implements MetadataStore {

    private final HashMap<Long, byte[]> meta = new HashMap<>();

    @Override
    public void put(int key, int version, @Nonnull byte[] data) {
        meta.put(toLong(key, version), data);
    }

    @Override
    @CheckForNull
    public byte[] get(int key, int version) {
        return meta.get(toLong(key, version));
    }

    @Override
    public boolean contains(int key, int version) {
        return meta.containsKey(toLong(key, version));
    }

    private long toLong(int key, int version) {
        return (((long) key) << 32) | version;
    }
}
