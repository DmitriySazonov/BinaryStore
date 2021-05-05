package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.ByteBufferHelper;

import javax.annotation.Nonnull;

public final class StringBinaryAdapter extends AbstractBinaryAdapter<String> {

    private static final String EMPTY = "";
    private static final Key.Byte ID = DefaultAdapters.STRING;
    public static final AdapterFactory<String, StringBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new StringBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull String value) {
        return ByteBuffer.INTEGER_BYTES + ByteBufferHelper.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull String value) {
        final int length = value.length();
        byteBuffer.write(length);
        if (length == 0) return;
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public String deserialize(@Nonnull ByteBuffer byteBuffer) {
        final int length = byteBuffer.readInt();
        if (length == 0) return EMPTY;
        return byteBuffer.readString(length);
    }
}
