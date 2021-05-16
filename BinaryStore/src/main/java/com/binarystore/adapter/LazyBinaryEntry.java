package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;

import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class LazyBinaryEntry<T> implements BinaryAdapter<T> {

    public final StaticByteBuffer buffer;
    private final BinaryAdapter<T> adapter;

    @CheckForNull
    private T object = null;
    private boolean hasValue = false;

    public LazyBinaryEntry(
            StaticByteBuffer buffer,
            BinaryAdapter<T> adapter
    ) {
        this.buffer = buffer;
        this.adapter = adapter;
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return adapter.key();
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Object value) throws Exception {
        byteBuffer.write(buffer);
    }

    @Override
    public int getSize(@Nonnull Object value) throws Exception {
        return buffer.getSize();
    }

    @Nonnull
    public final synchronized T get() throws Exception {
        if (!hasValue) {
            object = adapter.deserialize(buffer);
            hasValue = true;
        }
        return Objects.requireNonNull(object);
    }

    public boolean hasValue() {
        return hasValue;
    }

    @Nonnull
    @Override
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return get();
    }
}
