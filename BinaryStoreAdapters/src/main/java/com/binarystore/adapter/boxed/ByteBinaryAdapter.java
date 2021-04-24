package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

public class ByteBinaryAdapter extends BaseBinaryAdapter<Byte> {

    private static final Key.Int ID = DefaultAdapters.BYTE;

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.BYTE_BYTES;
    public static final AdapterFactory<Byte, ByteBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new ByteBinaryAdapter());

    @Override
    public Key.Int key() {
        return ID;
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
}
