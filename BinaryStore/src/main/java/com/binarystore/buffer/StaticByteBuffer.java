package com.binarystore.buffer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class StaticByteBuffer implements ByteBuffer {

    @Nonnull
    public final Meta meta;

    public final byte[] bytes;
    private final int start;
    private final int end;
    private final int size;

    private int absoluteOffset = 0;

    public StaticByteBuffer(int initialSize) {
        this(new byte[initialSize], 0, initialSize - 1);
    }

    public StaticByteBuffer(byte[] bytes, int start) {
        this(bytes, start, bytes.length - 1);
    }

    public StaticByteBuffer(byte[] bytes, int start, int end) {
        this(bytes, start, end, null);
    }

    public StaticByteBuffer(byte[] bytes, int start, int end, @CheckForNull Meta meta) {
        this.bytes = bytes;
        this.start = start;
        this.end = end;
        this.size = end - start + 1;
        this.absoluteOffset = start;
        this.meta = meta == null ? new Meta(new char[0]) : meta;
    }

    @Override
    public StaticByteBuffer getSubBuffer(int start, int end) {
        return new StaticByteBuffer(bytes, this.start + start, this.start + end, meta);
    }

    final byte[] getBytes() {
        return bytes;
    }

    @Override
    public int getAbsoluteOffset() {
        return absoluteOffset;
    }

    @Override
    public final int getOffset() {
        return absoluteOffset - start;
    }

    @Override
    public final int getSize() {
        return size;
    }

    @Override
    public final void setOffset(final int offset) {
        absoluteOffset = start + offset;
    }

    @Override
    public final void moveOffset(final int offset) {
        absoluteOffset += offset;
    }

    @Override
    public final void write(final char value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += CHAR_BYTES;
    }

    @Override
    public final void write(final boolean value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BOOLEAN_BYTES;
    }

    @Override
    public final void write(final byte value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BYTE_BYTES;
    }

    @Override
    public final void write(final short value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += SHORT_BYTES;
    }

    @Override
    public final void write(final int value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += INTEGER_BYTES;
    }

    @Override
    public final void write(final long value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += LONG_BYTES;
    }

    @Override
    public final void write(final float value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += FLOAT_BYTES;
    }

    @Override
    public final void write(final double value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += DOUBLE_BYTES;
    }

    @Override
    public final void write(final byte[] value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BYTE_BYTES * value.length;
    }

    @Override
    public final void write(@Nonnull final String value) {
        final int length = value.length();
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += CHAR_BYTES * length;
        if (meta.maxCharBufferLength < length) {
            meta.maxCharBufferLength = length;
        }
    }

    @Override
    public final void write(@Nonnull StaticByteBuffer value) {
        write(value.getBytes());
    }

    @Override
    public final char readChar() {
        int oldOffset = absoluteOffset;
        absoluteOffset += CHAR_BYTES;
        return ByteBufferHelper.readChar(bytes, oldOffset);
    }

    @Override
    public final byte readByte() {
        int oldOffset = absoluteOffset;
        absoluteOffset += BYTE_BYTES;
        return ByteBufferHelper.readByte(bytes, oldOffset);
    }

    @Override
    public final boolean readBoolean() {
        int oldOffset = absoluteOffset;
        absoluteOffset += BOOLEAN_BYTES;
        return ByteBufferHelper.readBoolean(bytes, oldOffset);
    }

    @Override
    public final short readShort() {
        int oldOffset = absoluteOffset;
        absoluteOffset += SHORT_BYTES;
        return ByteBufferHelper.readShort(bytes, oldOffset);
    }

    @Override
    public final int readInt() {
        int oldOffset = absoluteOffset;
        absoluteOffset += INTEGER_BYTES;
        return ByteBufferHelper.readInt(bytes, oldOffset);
    }

    @Override
    public final long readLong() {
        int oldOffset = absoluteOffset;
        absoluteOffset += LONG_BYTES;
        return ByteBufferHelper.readLong(bytes, oldOffset);
    }

    @Override
    public final float readFloat() {
        int oldOffset = absoluteOffset;
        absoluteOffset += FLOAT_BYTES;
        return ByteBufferHelper.readFloat(bytes, oldOffset);
    }

    @Override
    public final double readDouble() {
        int oldOffset = absoluteOffset;
        absoluteOffset += DOUBLE_BYTES;
        return ByteBufferHelper.readDouble(bytes, oldOffset);
    }

    @Override
    public final void readBytes(final byte[] dst) {
        ByteBufferHelper.readBytes(bytes, absoluteOffset, dst);
        absoluteOffset += dst.length;
    }

    @Override
    public final String readString(final int length) {
        int oldOffset = absoluteOffset;
        absoluteOffset += CHAR_BYTES * length;
        meta.ensureCharBufferLength(length);
        return ByteBufferHelper.readString(bytes, oldOffset, length, meta.charBuffer);
    }

    @Nonnull
    @Override
    public InputStream reserveInputStream(int size) {
        final ByteArrayInputStream stream =
                new ByteArrayInputStream(bytes, absoluteOffset, size);
        absoluteOffset += size * BYTE_BYTES;
        return stream;
    }

    @Nonnull
    @Override
    public OutputStream reserveOutputStream(int size) {
        final InnerByteArrayOutputStream stream =
                new InnerByteArrayOutputStream(bytes, absoluteOffset, size);
        absoluteOffset += size * BYTE_BYTES;
        return stream;
    }

    private final static class InnerByteArrayOutputStream extends OutputStream {

        private int offset;
        private final int end;
        private final byte[] bytes;

        InnerByteArrayOutputStream(byte[] bytes, int offset, int length) {
            this.bytes = bytes;
            this.offset = offset;
            this.end = offset + length;
        }

        public final void write(int value) {
            if (offset + 1 > end) {
                throw new IndexOutOfBoundsException();
            }
            bytes[offset] = (byte) value;
            ++offset;
        }

        public final void write(@Nonnull byte[] value, int start, int length) {
            final int rangeEnd = start + length;
            final boolean valueIndexesBad = start < 0 || start > rangeEnd
                    || rangeEnd > value.length;
            final boolean outOfBound = (offset + length) > this.end;
            if (valueIndexesBad || outOfBound) {
                throw new IndexOutOfBoundsException();
            }
            System.arraycopy(value, start, bytes, offset, length);
            offset += length;
        }
    }
}
