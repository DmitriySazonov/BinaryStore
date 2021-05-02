package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class LongBinaryAdapter extends AbstractBinaryAdapter<Long> {

    private static final Key.Int ID = DefaultAdapters.LONG;

    public static final AdapterFactory<Long, LongBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new LongBinaryAdapter());

    @Nonnull
    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Long value) throws Exception {
        return ByteBuffer.LONG_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Long value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Long deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readLong();
    }
}
