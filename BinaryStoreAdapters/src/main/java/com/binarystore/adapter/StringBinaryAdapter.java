package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.ByteBufferHelper;

import javax.annotation.Nonnull;

public final class StringBinaryAdapter extends AbstractBinaryAdapter<String> {

    private static final String EMPTY = "";
    private static final Key.Byte ID = DefaultAdapters.STRING;

    private static final int NULL_SIZE = com.binarystore.buffer.ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_HEADER_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<String, StringBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new StringBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull String value) {
        return value == null ? NULL_SIZE : FULL_HEADER_SIZE +
                ByteBufferHelper.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull String value) {
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

    @Nonnull
    @Override
    public String deserialize(@Nonnull ByteBuffer byteBuffer) {
        if (!byteBuffer.readBoolean()) return null;
        final int length = byteBuffer.readInt();
        if (length == 0) return EMPTY;
        return byteBuffer.readString(length);
    }
}
