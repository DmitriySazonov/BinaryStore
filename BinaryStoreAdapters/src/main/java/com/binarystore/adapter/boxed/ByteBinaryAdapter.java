package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.ByteBuffer;
import com.binarystore.adapter.DefaultAdapters;

public class ByteBinaryAdapter implements BinaryAdapter<Byte> {

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.BYTE_BYTES;
    public static final AdapterFactory<Byte> factory = context -> new ByteBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.INT;
    }

    @Override
    public int getSize(Byte value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Byte value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Byte deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readByte() : null;
    }

    @Override
    public Byte[] createArray(int size) throws Exception {
        return new Byte[size];
    }
}
