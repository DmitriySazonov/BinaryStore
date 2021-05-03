package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class NullBinaryAdapter implements BinaryAdapter<NullBinaryAdapter.Null> {

    private static final Key.Byte ID = DefaultAdapters.NULL;

    public static final AdapterFactory<Null, NullBinaryAdapter> factory =
            new SingletonAdapterFactory<>(ID, new NullBinaryAdapter());

    public static class Null {
        @SuppressWarnings("InstantiationOfUtilityClass")
        public static final Null instance = new Null();

        private Null() {
        }
    }

    public static Class<?> NULL_CLASS = Null.class;
    public static NullBinaryAdapter instance = new NullBinaryAdapter();

    @Nonnull
    @Override
    public Key.Byte key() {
        return ID;
    }

    @Override
    public int getSize(@CheckForNull Null value) throws Exception {
        return 0;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @CheckForNull Null value) throws Exception {

    }

    @Override
    @CheckForNull
    public Null deserialize(@CheckForNull ByteBuffer byteBuffer) throws Exception {
        return null;
    }
}
