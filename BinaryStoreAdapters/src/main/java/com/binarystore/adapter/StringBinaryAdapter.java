package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

public final class StringBinaryAdapter implements BinaryAdapter<String> {

    private static final int NULL_SIZE = com.binarystore.buffer.ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_HEADER_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<String> factory = context -> new StringBinaryAdapter();

    @Override
    public int id() {
        return DefaultAdapters.STRING;
    }

    @Override
    public int getSize(String value) {
        return value == null ? NULL_SIZE : FULL_HEADER_SIZE + (value.length() * 2);
    }

    @Override
    public void serialize(com.binarystore.buffer.ByteBuffer byteBuffer, String value) {
        if (value == null) {
            byteBuffer.write(false);
            return;
        }
        byteBuffer.write(true);
        final int len = value.length();
        final byte[] bytes = new byte[len * 2];
        char curChar;
        for (int i = 0, j = 0; i < len; i++) {
            curChar = value.charAt(i);
            bytes[j++] = (byte) (curChar);
            bytes[j++] = (byte) (curChar >>> 8);
        }
        byteBuffer.write(len);
        byteBuffer.write(bytes);
    }

    @Override
    public String deserialize(com.binarystore.buffer.ByteBuffer byteBuffer) {
        if (!byteBuffer.readBoolean()) return null;
        final int length = byteBuffer.readInt();
        final byte[] bytes = new byte[length * 2];
        byteBuffer.readBytes(bytes);

        final char[] chars = new char[length];
        for (int i = 0, j = 0; i < length; i++) {
            chars[i] = (char) (((bytes[j++] & 0xFF)) | ((bytes[j++] & 0xFF) << 8));
        }
        return new String(chars);
    }
}
