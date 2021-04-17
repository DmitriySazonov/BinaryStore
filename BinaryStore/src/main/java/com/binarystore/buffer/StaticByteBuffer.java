package com.binarystore.buffer;

public class StaticByteBuffer implements ByteBuffer {

    private final byte[] bytes;
    private final int start;
    private final int end;
    private final int size;

    private int absoluteOffset = 0;

    public StaticByteBuffer(int initialSize) {
        this(new byte[initialSize], 0, initialSize - 1);
    }

    public StaticByteBuffer(byte[] bytes, int start, int end) {
        this.bytes = bytes;
        this.start = start;
        this.end = end;
        this.size = end - start;
        this.absoluteOffset = start;
    }

    @Override
    public int getOffset() {
        return absoluteOffset - start;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setOffset(final int offset) {
        if (offset > end) {
            absoluteOffset = end;
        } else {
            absoluteOffset = Math.max(offset, start);
        }
    }

    @Override
    public void moveOffset(final int offset) {
        setOffset(absoluteOffset + offset);
    }

    @Override
    public void write(final boolean value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BOOLEAN_BYTES;
    }

    @Override
    public void write(final byte value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BYTE_BYTES;
    }

    @Override
    public void write(final short value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += SHORT_BYTES;
    }

    @Override
    public void write(final int value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += INTEGER_BYTES;
    }

    @Override
    public void write(final long value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += LONG_BYTES;
    }

    @Override
    public void write(final float value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += FLOAT_BYTES;
    }

    @Override
    public void write(final double value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += DOUBLE_BYTES;
    }

    @Override
    public void write(final byte[] value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += BYTE_BYTES * value.length;
    }

    @Override
    public void write(final String value) {
        ByteBufferHelper.write(bytes, absoluteOffset, value);
        absoluteOffset += CHAR_BYTES * value.length();
    }

    @Override
    public byte readByte() {
        int oldOffset = absoluteOffset;
        absoluteOffset += BYTE_BYTES;
        return ByteBufferHelper.readByte(bytes, oldOffset);
    }

    @Override
    public boolean readBoolean() {
        int oldOffset = absoluteOffset;
        absoluteOffset += BOOLEAN_BYTES;
        return ByteBufferHelper.readBoolean(bytes, oldOffset);
    }

    @Override
    public short readShort() {
        int oldOffset = absoluteOffset;
        absoluteOffset += SHORT_BYTES;
        return ByteBufferHelper.readShort(bytes, oldOffset);
    }

    @Override
    public int readInt() {
        int oldOffset = absoluteOffset;
        absoluteOffset += INTEGER_BYTES;
        return ByteBufferHelper.readInt(bytes, oldOffset);
    }

    @Override
    public long readLong() {
        int oldOffset = absoluteOffset;
        absoluteOffset += LONG_BYTES;
        return ByteBufferHelper.readLong(bytes, oldOffset);
    }

    @Override
    public float readFloat() {
        int oldOffset = absoluteOffset;
        absoluteOffset += FLOAT_BYTES;
        return ByteBufferHelper.readFloat(bytes, oldOffset);
    }

    @Override
    public double readDouble() {
        int oldOffset = absoluteOffset;
        absoluteOffset += DOUBLE_BYTES;
        return ByteBufferHelper.readDouble(bytes, oldOffset);
    }

    @Override
    public void readBytes(final byte[] dst) {
        ByteBufferHelper.readBytes(bytes, absoluteOffset, dst);
        absoluteOffset += dst.length;
    }

    @Override
    public String readString(final int length) {
        int oldOffset = absoluteOffset;
        absoluteOffset += CHAR_BYTES * length;
        return ByteBufferHelper.readString(bytes, oldOffset, length);
    }
}
