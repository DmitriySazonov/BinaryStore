package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class BooleanBinaryAdapter extends AbstractBinaryAdapter<Boolean> {

    private static final Key.Int ID = DefaultAdapters.BOOLEAN;

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;
    public static final AdapterFactory<Boolean, BooleanBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new BooleanBinaryAdapter());

    @Nonnull
    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Boolean value) {
        return ByteBuffer.BYTE_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Boolean value) {
        byteBuffer.write(value ? TRUE : FALSE);
    }

    @Nonnull
    @Override
    public Boolean deserialize(@Nonnull ByteBuffer byteBuffer) {
        return byteBuffer.readByte() == TRUE;
    }
}
