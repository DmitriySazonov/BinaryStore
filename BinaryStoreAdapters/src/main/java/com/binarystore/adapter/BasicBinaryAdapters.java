package com.binarystore.adapter;

import com.binarystore.adapter.boxed.BooleanBinaryAdapter;
import com.binarystore.adapter.boxed.ByteBinaryAdapter;
import com.binarystore.adapter.boxed.CharBinaryAdapter;
import com.binarystore.adapter.boxed.DoubleBinaryAdapter;
import com.binarystore.adapter.boxed.FloatBinaryAdapter;
import com.binarystore.adapter.boxed.IntBinaryAdapter;
import com.binarystore.adapter.boxed.LongBinaryAdapter;
import com.binarystore.adapter.boxed.ShortBinaryAdapter;
import com.binarystore.adapter.collection.map.ConcurrentHashMapBinaryAdapter;
import com.binarystore.adapter.collection.map.ConcurrentSkipListMapBinaryAdapter;
import com.binarystore.adapter.collection.map.EnumMapBinaryAdapter;
import com.binarystore.adapter.collection.map.HashMapBinaryAdapter;
import com.binarystore.adapter.collection.map.LinkedHashMapBinaryAdapter;
import com.binarystore.adapter.collection.map.TreeMapBinaryAdapter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BasicBinaryAdapters {

    public static void registerInto(AdapterFactoryRegister register) {
        register.register(Character.class, CharBinaryAdapter.factory);
        register.register(Boolean.class, BooleanBinaryAdapter.factory);
        register.register(Byte.class, ByteBinaryAdapter.factory);
        register.register(Short.class, ShortBinaryAdapter.factory);
        register.register(Integer.class, IntBinaryAdapter.factory);
        register.register(Long.class, LongBinaryAdapter.factory);
        register.register(Float.class, FloatBinaryAdapter.factory);
        register.register(Double.class, DoubleBinaryAdapter.factory);

        register.register(String.class, StringBinaryAdapter.factory);

        register.register(TreeMap.class, TreeMapBinaryAdapter.factory);
        register.register(HashMap.class, HashMapBinaryAdapter.factory);
        register.register(LinkedHashMap.class, LinkedHashMapBinaryAdapter.factory);
        register.register(ConcurrentHashMap.class, ConcurrentHashMapBinaryAdapter.factory);
        register.register(ConcurrentSkipListMap.class, ConcurrentSkipListMapBinaryAdapter.factory);
        register.register(EnumMap.class, EnumMapBinaryAdapter.factory);


        register.register(Class.class, ClassBinaryAdapter.factory);
    }
}
