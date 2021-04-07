package com.binarystore.adapter;

public class IdProvider {

    private static final int DEFAULT_MODULE = 255;

    static IdProvider defaultProvider = new IdProvider(DEFAULT_MODULE);

    static int sId(int order) {
        return defaultProvider.id(order);
    }

    private final int module;

    public IdProvider(byte module) {
        this((int) module);
        if (module == (byte) DEFAULT_MODULE) {
            throw new IllegalArgumentException("value 255 reserved for default adapters");
        }
    }

    private IdProvider(int module) {
        this.module = module << 24;
    }

    public int id(int order) {
        return module + order;
    }
}
