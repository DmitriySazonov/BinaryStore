package com.binarystore.adapter;

import android.graphics.Bitmap;

import com.binarystore.adapter.bitmap.BitmapBinaryAdapter;

public final class AndroidBasicBinaryAdapters {

    public static void registerInto(AdapterFactoryRegister register) {
        register.register(Bitmap.class, BitmapBinaryAdapter.factory);
    }
}
