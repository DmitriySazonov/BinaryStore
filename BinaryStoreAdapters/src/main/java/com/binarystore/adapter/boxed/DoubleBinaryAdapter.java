package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class DoubleBinaryAdapter implements BinaryAdapter<Double> {

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.DOUBLE_BYTES;
    public static final AdapterFactory<Double> factory = context -> new DoubleBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.DOUBLE;
    }

    @Override
    public int getSize(Double value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Double value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Double deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readDouble() : null;
    }

    @Override
    public Double[] createArray(int size) throws Exception {
        return new Double[size];
    }
}
