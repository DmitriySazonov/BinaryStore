package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class ByteBinaryAdapter extends AbstractBinaryAdapter<Byte> {

    private static final Key.Int ID = DefaultAdapters.BYTE;

    public static final AdapterFactory<Byte, ByteBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new ByteBinaryAdapter());

    @Nonnull
    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Byte value) throws Exception {
        return ByteBuffer.BYTE_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Byte value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Byte deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readByte();
    }
}
