package com.binarystore.buffer;

public class JavaByteBuffer implements ByteBuffer {

    private java.nio.ByteBuffer byteBuffer;

    public JavaByteBuffer(int size) {
        byteBuffer = java.nio.ByteBuffer.allocate(size);
    }

    public JavaByteBuffer(java.nio.ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public void write(char c) {
        byteBuffer.putChar(c);
    }

    public char readChar() {
        return byteBuffer.getChar();
    }

    @Override
    public int getOffset() {
        return byteBuffer.arrayOffset();
    }

    @Override
    public int getSize() {
        return byteBuffer.capacity();
    }

    @Override
    public void setOffset(int offset) {
        byteBuffer.position(offset);
    }

    @Override
    public void moveOffset(int offset) {
        byteBuffer.position(byteBuffer.arrayOffset() + offset);
    }

    @Override
    public void write(boolean value) {
        byteBuffer.put((byte) (value ? 1 : 0));
    }

    @Override
    public void write(byte value) {
        byteBuffer.put(value);
    }

    @Override
    public void write(short value) {
        byteBuffer.putShort(value);
    }

    @Override
    public void write(int value) {
        byteBuffer.putInt(value);
    }

    @Override
    public void write(long value) {
        byteBuffer.putLong(value);
    }

    @Override
    public void write(float value) {
        byteBuffer.putFloat(value);
    }

    @Override
    public void write(double value) {
        byteBuffer.putDouble(value);
    }

    @Override
    public void write(byte[] value) {
        byteBuffer.put(value);
    }

    @Override
    public void write(String value) {
        final byte[] bytes = new byte[value.length() * CHAR_BYTES];
        ByteBufferHelper.write(bytes, 0, value);
        byteBuffer.put(bytes);
    }

    @Override
    public byte readByte() {
        return byteBuffer.get();
    }

    @Override
    public boolean readBoolean() {
        return byteBuffer.get() == 1;
    }

    @Override
    public short readShort() {
        return byteBuffer.getShort();
    }

    @Override
    public int readInt() {
        return byteBuffer.getInt();
    }

    @Override
    public long readLong() {
        return byteBuffer.getLong();
    }

    @Override
    public float readFloat() {
        return byteBuffer.getFloat();
    }

    @Override
    public double readDouble() {
        return byteBuffer.getDouble();
    }

    @Override
    public void readBytes(byte[] dst) {
        byteBuffer.get(dst);
    }

    @Override
    public String readString(int length) {
        final byte[] bytes = new byte[length * CHAR_BYTES];
        readBytes(bytes);
        return ByteBufferHelper.readString(bytes, 0, length);
    }
}
