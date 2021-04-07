package com.example.binaryjson;

import java.util.Arrays;

public class DynamicByteBufferDep {

    public static final int BOOLEAN_BYTES = 1;
    public static final int BYTE_BYTES = 1;
    public static final int INTEGER_BYTES = 4;
    public static final int LONG_BYTES = 8;
    public static final int FLOAT_BYTES = 4;
    public static final int DOUBLE_BYTES = 8;

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    private byte[] bytes;
    private int size = -1;

    public DynamicByteBufferDep(int initialSize) {
        bytes = new byte[initialSize];
    }

    public DynamicByteBufferDep(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getOffset() {
        return size + 1;
    }

    public void setOffset(int offset) {
        this.size = offset - 1;
    }

    public void moveOffset(int offset) {
        this.size += offset;
    }

    public void write(boolean value) {
        checkFreeSpace(BOOLEAN_BYTES);
        bytes[++size] = value ? TRUE : FALSE;
    }

    public void write(byte value) {
        checkFreeSpace(BYTE_BYTES);
        bytes[++size] = value;
    }

    public void write(int value) {
        checkFreeSpace(INTEGER_BYTES);
        byte byte1 = (byte) value;
        byte byte2 = (byte) (value >> 8);
        byte byte3 = (byte) (value >> 16);
        byte byte4 = (byte) (value >> 24);
        bytes[++size] = byte4;
        bytes[++size] = byte3;
        bytes[++size] = byte2;
        bytes[++size] = byte1;
    }

    public void write(long value) {
        checkFreeSpace(LONG_BYTES);
        byte byte1 = (byte) value;
        byte byte2 = (byte) (value >> 8);
        byte byte3 = (byte) (value >> 16);
        byte byte4 = (byte) (value >> 24);
        byte byte5 = (byte) (value >> 32);
        byte byte6 = (byte) (value >> 40);
        byte byte7 = (byte) (value >> 48);
        byte byte8 = (byte) (value >> 56);
        bytes[++size] = byte8;
        bytes[++size] = byte7;
        bytes[++size] = byte6;
        bytes[++size] = byte5;
        bytes[++size] = byte4;
        bytes[++size] = byte3;
        bytes[++size] = byte2;
        bytes[++size] = byte1;
    }

    public void write(float value) {
        write(Float.floatToRawIntBits(value));
    }

    public void write(double value) {
        write(Double.doubleToRawLongBits(value));
    }

    public void write(final byte[] value) {
        checkFreeSpace(value.length);
        System.arraycopy(value, 0, bytes, size + 1, value.length);
        size += value.length;
    }

    public byte readByte() {
        return bytes[++size];
    }

    public boolean readBoolean() {
        return bytes[++size] == TRUE;
    }

    public int readInt() {
        return makeInt(
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size]
        );
    }

    public long readLong() {
        return makeLong(
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size],
                bytes[++size]
        );
    }

    public float readFloat() {
        int value = readInt();
        return Float.intBitsToFloat(value);
    }

    public double readDouble() {
        long value = readLong();
        return Double.longBitsToDouble(value);
    }

    public void readBytes(byte[] dst) {
        System.arraycopy(bytes, size + 1, dst, 0, dst.length);
        size += dst.length;
    }

    private void checkFreeSpace(int needSpace) {
        boolean hasFreeSpace = bytes.length - size > needSpace;
        if (hasFreeSpace) return;
        bytes = Arrays.copyOf(bytes, bytes.length * 2);
    }

    private static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    private static long makeLong(byte b7, byte b6, byte b5, byte b4,
                                 byte b3, byte b2, byte b1, byte b0) {
        return ((((long) b7) << 56) |
                (((long) b6 & 0xff) << 48) |
                (((long) b5 & 0xff) << 40) |
                (((long) b4 & 0xff) << 32) |
                (((long) b3 & 0xff) << 24) |
                (((long) b2 & 0xff) << 16) |
                (((long) b1 & 0xff) << 8) |
                (((long) b0 & 0xff)));
    }
}
