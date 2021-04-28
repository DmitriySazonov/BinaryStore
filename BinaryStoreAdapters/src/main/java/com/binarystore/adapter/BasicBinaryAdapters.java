package com.binarystore.adapter;

import com.binarystore.adapter.collection.map.HashMapBinaryAdapter;
import com.binarystore.adapter.collection.map.TreeMapBinaryAdapter;

import java.util.HashMap;
import java.util.TreeMap;

public class BasicBinaryAdapters {

    public static void registerInto(AdapterFactoryRegister register) {
        register.register(String.class, StringBinaryAdapter.factory);

        register.register(TreeMap.class, TreeMapBinaryAdapter.factory);
        register.register(HashMap.class, HashMapBinaryAdapter.factory);
    }
}
