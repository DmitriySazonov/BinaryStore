package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class BooleanBinaryAdapter implements BinaryAdapter<Boolean> {

    private static final byte NULL = -1;
    private static final byte TRUE = 1;
    private static final byte FALSE = 0;
    public static final AdapterFactory<Boolean> factory = context -> new BooleanBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.BOOLEAN;
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

    @Override
    public Boolean[] createArray(int size) throws Exception {
        return new Boolean[size];
    }
}
