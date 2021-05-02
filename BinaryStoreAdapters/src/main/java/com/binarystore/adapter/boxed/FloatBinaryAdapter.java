package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class FloatBinaryAdapter extends AbstractBinaryAdapter<Float> {

    private static final Key.Int ID = DefaultAdapters.FLOAT;

    public static final AdapterFactory<Float, FloatBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new FloatBinaryAdapter());

    @Nonnull
    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Float value) throws Exception {
        return ByteBuffer.FLOAT_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Float value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Float deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readFloat();
    }
}
