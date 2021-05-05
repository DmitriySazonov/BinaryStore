package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public final class IntBinaryAdapter extends AbstractBinaryAdapter<Integer> {

    private static final Key.Byte ID = DefaultAdapters.INT;

    public static final AdapterFactory<Integer, IntBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new IntBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Integer value) throws Exception {
        return ByteBuffer.INTEGER_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Integer value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Integer deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readInt();
    }
}
