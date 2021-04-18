package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

public final class StringBinaryAdapter extends BaseBinaryAdapter<String> {

    private static final String EMPTY = "";
    private static final Key.Int ID = DefaultAdapters.STRING;

    private static final int NULL_SIZE = com.binarystore.buffer.ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_HEADER_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<String, StringBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new StringBinaryAdapter());

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
        final int length = value.length();
        byteBuffer.write(length);
        if (length == 0) return;
        byteBuffer.write(value);
    }

    @Override
    public String deserialize(ByteBuffer byteBuffer) {
        if (!byteBuffer.readBoolean()) return null;
        final int length = byteBuffer.readInt();
        if (length == 0) return EMPTY;
        return byteBuffer.readString(length);
    }
}
