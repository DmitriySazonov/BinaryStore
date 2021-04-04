package com.binarystore.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MetadataStoreInMemory implements MetadataStore {

    private HashMap<Long, byte[]> meta = new HashMap<>();

    @Override
    public void put(int key, int version, @NotNull byte[] data) {
        meta.put(toLong(key, version), data);
    }

    @Override
    @Nullable
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
