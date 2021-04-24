package com.binarystore.adapter;

import com.binarystore.adapter.boxed.BooleanBinaryAdapter;
import com.binarystore.adapter.boxed.ByteBinaryAdapter;
import com.binarystore.adapter.boxed.DoubleBinaryAdapter;
import com.binarystore.adapter.boxed.FloatBinaryAdapter;
import com.binarystore.adapter.boxed.IntBinaryAdapter;
import com.binarystore.adapter.boxed.LongBinaryAdapter;
import com.binarystore.adapter.boxed.ShortBinaryAdapter;
import com.binarystore.adapter.collection.map.HashMapBinaryAdapter;

import java.util.HashMap;

public class BasicBinaryAdapters {

    public static void registerInto(AdapterFactoryRegister register) {
        register.register(Boolean.class, BooleanBinaryAdapter.factory);
        register.register(Byte.class, ByteBinaryAdapter.factory);
        register.register(Short.class, ShortBinaryAdapter.factory);
        register.register(Integer.class, IntBinaryAdapter.factory);
        register.register(Long.class, LongBinaryAdapter.factory);
        register.register(Float.class, FloatBinaryAdapter.factory);
        register.register(Double.class, DoubleBinaryAdapter.factory);
        register.register(String.class, StringBinaryAdapter.factory);

        register.register(HashMap.class, HashMapBinaryAdapter.factory);
    }
}
