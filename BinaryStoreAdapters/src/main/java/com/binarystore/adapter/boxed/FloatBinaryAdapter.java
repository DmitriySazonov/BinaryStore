package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class FloatBinaryAdapter implements BinaryAdapter<Float> {

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.FLOAT_BYTES;
    public static final AdapterFactory<Float> factory = context -> new FloatBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.FLOAT;
    }

    @Override
    public int getSize(Float value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Float value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Float deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readFloat() : null;
    }

    @Override
    public Float[] createArray(int size) throws Exception {
        return new Float[size];
    }
}
