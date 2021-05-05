package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.SingletonAdapterFactory;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class CharBinaryAdapter extends AbstractBinaryAdapter<Character> {

    private static final Key.Byte ID = DefaultAdapters.CHAR;
    public static final AdapterFactory<Character, CharBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new CharBinaryAdapter());

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@Nonnull Character value) throws Exception {
        return ByteBuffer.CHAR_BYTES;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Character value) throws Exception {
        byteBuffer.write(value);
    }

    @Nonnull
    @Override
    public Character deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readChar();
    }
}
