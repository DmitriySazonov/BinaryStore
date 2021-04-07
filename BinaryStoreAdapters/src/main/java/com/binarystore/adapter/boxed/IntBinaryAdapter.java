package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class IntBinaryAdapter implements BinaryAdapter<Integer> {

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<Integer> factory = context -> new IntBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.INT;
    }

    @Override
    public int getSize(Integer value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Integer value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Integer deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readInt() : null;
    }
}
