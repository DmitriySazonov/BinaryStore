package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

public class BooleanBinaryAdapter extends BaseBinaryAdapter<Boolean> {

    private static final Key.Int ID = DefaultAdapters.BOOLEAN;

    private static final byte NULL = -1;
    private static final byte TRUE = 1;
    private static final byte FALSE = 0;
    public static final AdapterFactory<Boolean, BooleanBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new BooleanBinaryAdapter());

    @Override
    public Key.Int key() {
        return ID;
    }

    @Override
    public int getSize(Boolean value) {
        return ByteBuffer.BYTE_BYTES;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Boolean value) {
        byteBuffer.write(value == null ? NULL : value ? TRUE : FALSE);
    }

    @Override
    public Boolean deserialize(ByteBuffer byteBuffer) {
        byte value = byteBuffer.readByte();
        return value == NULL ? null : value == TRUE;
    }
}
