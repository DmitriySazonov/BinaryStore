package com.binarystore.buffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class DynamicByteBuffer implements ByteBuffer {

    @Nonnull
    public final Meta meta;

    private byte[] bytes;
    private int offset = 0;

    public DynamicByteBuffer(int initialSize) {
        this(new byte[initialSize], null);
    }

    public DynamicByteBuffer(byte[] bytes, @CheckForNull Meta meta) {
        this.bytes = bytes;
        this.meta = meta == null ? new Meta(new char[0]) : meta;
    }

    @Override
    public StaticByteBuffer getSubBuffer(int start, int end) {
        checkFreeSpace(end - Math.max(offset, start));
        return new StaticByteBuffer(bytes, start, end);
    }

    @Override
    public int getAbsoluteOffset() {
        return offset;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getSize() {
        return bytes.length;
    }

    @Override
    public void setOffset(final int offset) {
        this.offset = offset;
    }

    @Override
    public void moveOffset(final int offset) {
        this.offset += offset;
    }

    @Override
    public void write(final char value) {
        checkFreeSpace(CHAR_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += CHAR_BYTES;
    }

    @Override
    public void write(final boolean value) {
        checkFreeSpace(BOOLEAN_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += BOOLEAN_BYTES;
    }

    @Override
    public void write(final byte value) {
        checkFreeSpace(BYTE_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += BYTE_BYTES;
    }

    @Override
    public void write(final short value) {
        checkFreeSpace(SHORT_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += SHORT_BYTES;
    }

    @Override
    public void write(final int value) {
        checkFreeSpace(INTEGER_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += INTEGER_BYTES;
    }

    @Override
    public void write(final long value) {
        checkFreeSpace(LONG_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += LONG_BYTES;
    }

    @Override
    public void write(final float value) {
        checkFreeSpace(FLOAT_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += FLOAT_BYTES;
    }

    @Override
    public void write(final double value) {
        checkFreeSpace(DOUBLE_BYTES);
        ByteBufferHelper.write(bytes, offset, value);
        offset += DOUBLE_BYTES;
    }

    @Override
    public void write(final byte[] value) {
        checkFreeSpace(BYTE_BYTES * value.length);
        ByteBufferHelper.write(bytes, offset, value);
        offset += BYTE_BYTES * value.length;
    }

    @Override
    public void write(@Nonnull final String value) {
        final int length = value.length();
        checkFreeSpace(CHAR_BYTES * length);
        ByteBufferHelper.write(bytes, offset, value);
        offset += CHAR_BYTES * value.length();
        if (meta.maxCharBufferLength < length) {
            meta.maxCharBufferLength = length;
        }
    }

    @Override
    public void write(@Nonnull StaticByteBuffer value) {
        write(value.getBytes());
    }

    @Override
    public char readChar() {
        int oldOffset = offset;
        offset += CHAR_BYTES;
        return ByteBufferHelper.readChar(bytes, oldOffset);
    }

    @Override
    public byte readByte() {
        int oldOffset = offset;
        offset += BYTE_BYTES;
        return ByteBufferHelper.readByte(bytes, oldOffset);
    }

    @Override
    public boolean readBoolean() {
        int oldOffset = offset;
        offset += BOOLEAN_BYTES;
        return ByteBufferHelper.readBoolean(bytes, oldOffset);
    }

    @Override
    public short readShort() {
        int oldOffset = offset;
        offset += SHORT_BYTES;
        return ByteBufferHelper.readShort(bytes, oldOffset);
    }

    @Override
    public int readInt() {
        int oldOffset = offset;
        offset += INTEGER_BYTES;
        return ByteBufferHelper.readInt(bytes, oldOffset);
    }

    @Override
    public long readLong() {
        int oldOffset = offset;
        offset += LONG_BYTES;
        return ByteBufferHelper.readLong(bytes, oldOffset);
    }

    @Override
    public float readFloat() {
        int oldOffset = offset;
        offset += FLOAT_BYTES;
        return ByteBufferHelper.readFloat(bytes, oldOffset);
    }

    @Override
    public double readDouble() {
        int oldOffset = offset;
        offset += DOUBLE_BYTES;
        return ByteBufferHelper.readDouble(bytes, oldOffset);
    }

    @Override
    public void readBytes(final byte[] dst) {
        ByteBufferHelper.readBytes(bytes, offset, dst);
        offset += dst.length;
    }

    @Override
    public String readString(final int length) {
        int oldOffset = offset;
        offset += CHAR_BYTES * length;
        meta.ensureCharBufferLength(length);
        return ByteBufferHelper.readString(bytes, oldOffset, length, meta.charBuffer);
    }

    @Nonnull
    @Override
    public ByteArrayInputStream reserveInputStream(int size) {
        return null;
    }

    @Nonnull
    @Override
    public ByteArrayOutputStream reserveOutputStream(int size) {
        return null;
    }

    private void checkFreeSpace(final int needSpace) {
        boolean hasFreeSpace = bytes.length - offset >= needSpace;
        if (hasFreeSpace) return;
        bytes = Arrays.copyOf(bytes, bytes.length * 2);
    }
}
