package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class LongBinaryAdapter implements BinaryAdapter<Long> {

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.LONG_BYTES;
    public static final AdapterFactory<Long> factory = context -> new LongBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.LONG;
    }

    @Override
    public int getSize(Long value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Long value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Long deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readLong() : null;
    }

    @Override
    public Long[] createArray(int size) throws Exception {
        return new Long[size];
    }
}
