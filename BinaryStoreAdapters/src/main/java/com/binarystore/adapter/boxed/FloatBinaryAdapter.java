package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

public class FloatBinaryAdapter extends BaseBinaryAdapter<Float> {

    private static final Key.Int ID = DefaultAdapters.FLOAT;

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.FLOAT_BYTES;
    public static final AdapterFactory<Float, FloatBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new FloatBinaryAdapter());

    @Override
    public Key.Int id() {
        return ID;
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
}
