package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class DoubleBinaryAdapter extends AbstractBinaryAdapter<Double> {

    private static final Key.Byte ID = DefaultAdapters.DOUBLE;

    public static final AdapterFactory<Double, DoubleBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new DoubleBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Double value) throws Exception {
        return ByteBuffer.DOUBLE_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Double value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Double deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readDouble();
    }
}
