package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

public class DoubleBinaryAdapter extends BaseBinaryAdapter<Double> {

    private static final Key.Int ID = DefaultAdapters.DOUBLE;

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.DOUBLE_BYTES;
    public static final AdapterFactory<Double, DoubleBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new DoubleBinaryAdapter());

    @Override
    public Key.Int key() {
        return ID;
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
}
