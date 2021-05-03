package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class ShortBinaryAdapter extends AbstractBinaryAdapter<Short> {

    private static final Key.Byte ID = DefaultAdapters.SHORT;

    public static final AdapterFactory<Short, ShortBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new ShortBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Short value) throws Exception {
        return ByteBuffer.SHORT_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Short value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Short deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readShort();
    }
}
