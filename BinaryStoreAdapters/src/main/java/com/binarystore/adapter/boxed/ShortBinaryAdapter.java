package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

public class ShortBinaryAdapter extends BaseBinaryAdapter<Short> {

    private static final Key.Int ID = DefaultAdapters.SHORT;

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.SHORT_BYTES;
    public static final AdapterFactory<Short, ShortBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new ShortBinaryAdapter());

    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(Short value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Short value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Short deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readShort() : null;
    }
}
