package com.binarystore.buffer;

public abstract class BaseByteBuffer implements ByteBuffer {

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    protected byte[] bytes;
    protected int start;
    protected int end;
    protected int offset = 0;

    protected BaseByteBuffer(byte[] bytes, int start, int end) {
        setSource(bytes, start, end);
    }

    protected void setSource(byte[] bytes, int start, int end) {
        this.bytes = bytes;
        this.start = start;
        this.end = end;
    }

    abstract void checkFreeSpace(int needSpace);

    public int getOffset() {
        return offset;
    }

    @Override
    public int getSize() {
        return end - start;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void moveOffset(int offset) {
        this.offset += offset;
    }

    public void write(boolean value) {
        checkFreeSpace(BOOLEAN_BYTES);
        bytes[start + offset++] = value ? TRUE : FALSE;
    }

    public void write(byte value) {
        checkFreeSpace(BYTE_BYTES);
        bytes[start + offset++] = value;
    }

    public void write(short value) {
        checkFreeSpace(SHORT_BYTES);
        byte byte1 = (byte) value;
        byte byte2 = (byte) (value >> 8);
        bytes[start + offset++] = byte2;
        bytes[start + offset++] = byte1;
    }

    public void write(int value) {
        checkFreeSpace(INTEGER_BYTES);
        byte byte1 = (byte) value;
        byte byte2 = (byte) (value >> 8);
        byte byte3 = (byte) (value >> 16);
        byte byte4 = (byte) (value >> 24);
        bytes[start + offset++] = byte4;
        bytes[start + offset++] = byte3;
        bytes[start + offset++] = byte2;
        bytes[start + offset++] = byte1;
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
        bytes[start + offset++] = byte8;
        bytes[start + offset++] = byte7;
        bytes[start + offset++] = byte6;
        bytes[start + offset++] = byte5;
        bytes[start + offset++] = byte4;
        bytes[start + offset++] = byte3;
        bytes[start + offset++] = byte2;
        bytes[start + offset++] = byte1;
    }

    public void write(float value) {
        write(Float.floatToRawIntBits(value));
    }

    public void write(double value) {
        write(Double.doubleToRawLongBits(value));
    }

    public void write(final byte[] value) {
        checkFreeSpace(value.length);
        System.arraycopy(value, 0, bytes, offset, value.length);
        offset += value.length;
    }

    public byte readByte() {
        return bytes[start + offset++];
    }

    public boolean readBoolean() {
        return bytes[start + offset++] == TRUE;
    }

    public short readShort() {
        return makeShort(
                bytes[start + offset++],
                bytes[start + offset++]
        );
    }

    public int readInt() {
        return makeInt(
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++]
        );
    }

    public long readLong() {
        return makeLong(
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++],
                bytes[start + offset++]
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
        System.arraycopy(bytes, offset, dst, 0, dst.length);
        offset += dst.length;
    }

    private static short makeShort(byte b1, byte b0) {
        return (short) (((((short) b1) & 0xff) << 8) | ((((short) b0) & 0xff)));
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
