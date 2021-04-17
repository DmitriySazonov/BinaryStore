package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public final class StringBinaryAdapter extends BaseBinaryAdapter<String> {

    private static final Key.Int ID = DefaultAdapters.STRING;

    private static final int NULL_SIZE = com.binarystore.buffer.ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_HEADER_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<String> factory = new AbstractAdapterFactory<String>(ID) {
        @Override
        @Nonnull
        public BinaryAdapter<String> create(@Nonnull Context context) {
            return new StringBinaryAdapter();
        }
    };

    @Override
    public Key.Int id() {
        return ID;
    }

    @Override
    public int getSize(String value) {
        return value == null ? NULL_SIZE : FULL_HEADER_SIZE + (value.length() * 2);
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, String value) {
        if (value == null) {
            byteBuffer.write(false);
            return;
        }
        byteBuffer.write(true);
        byteBuffer.write(value.length());
        byteBuffer.write(value);
    }

    @Override
    public String deserialize(ByteBuffer byteBuffer) {
        if (!byteBuffer.readBoolean()) return null;
        final int length = byteBuffer.readInt();
        return byteBuffer.readString(length);
    }
}
