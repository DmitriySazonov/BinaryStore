package com.binarystore.buffer;

public class ByteBufferHelper {

    private static final byte TRUE = 1;
    private static final byte FALSE = 0;

    public static void write(final byte[] bytes, int offset, final boolean value) {
        bytes[offset] = value ? TRUE : FALSE;
    }

    public static void write(final byte[] bytes, int offset, final byte value) {
        bytes[offset] = value;
    }

    public static void write(final byte[] bytes, int offset, final short value) {
        bytes[offset] = (byte) (value >> 8);
        bytes[++offset] = (byte) value;
    }

    public static void write(final byte[] bytes, int offset, final int value) {
        bytes[offset] = (byte) (value >> 24);
        bytes[++offset] = (byte) (value >> 16);
        bytes[++offset] = (byte) (value >> 8);
        bytes[++offset] = (byte) value;
    }

    public static void write(final byte[] bytes, int offset, final long value) {
        bytes[offset] = (byte) (value >> 56);
        bytes[++offset] = (byte) (value >> 48);
        bytes[++offset] = (byte) (value >> 40);
        bytes[++offset] = (byte) (value >> 32);
        bytes[++offset] = (byte) (value >> 24);
        bytes[++offset] = (byte) (value >> 16);
        bytes[++offset] = (byte) (value >> 8);
        bytes[++offset] = (byte) value;
    }

    public static void write(final byte[] bytes, int offset, final float value) {
        write(bytes, offset, Float.floatToRawIntBits(value));
    }

    public static void write(final byte[] bytes, int offset, final double value) {
        write(bytes, offset, Double.doubleToRawLongBits(value));
    }

    public static void write(final byte[] bytes, final int offset, final String value) {
        final int len = value.length();
        char curChar;
        for (int i = 0, j = offset; i < len; i++) {
            curChar = value.charAt(i);
            bytes[j++] = (byte) (curChar);
            bytes[j++] = (byte) (curChar >>> 8);
        }
    }

    public static int getSize(final String value) {
        return value.length() * ByteBuffer.CHAR_BYTES;
    }

    public static void write(final byte[] bytes, int offset, final byte[] value) {
        System.arraycopy(value, 0, bytes, offset, value.length);
    }

    public static byte readByte(final byte[] bytes, int offset) {
        return bytes[offset];
    }

    public static boolean readBoolean(final byte[] bytes, int offset) {
        return bytes[offset] == TRUE;
    }

    public static short readShort(final byte[] bytes, int offset) {
        return makeShort(
                bytes[offset],
                bytes[++offset]
        );
    }

    public static int readInt(byte[] bytes, int offset) {
        return makeInt(
                bytes[offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset]
        );
    }

    public static long readLong(final byte[] bytes, int offset) {
        return makeLong(
                bytes[offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset],
                bytes[++offset]
        );
    }

    public static float readFloat(final byte[] bytes, int offset) {
        int value = readInt(bytes, offset);
        return Float.intBitsToFloat(value);
    }

    public static double readDouble(final byte[] bytes, int offset) {
        long value = readLong(bytes, offset);
        return Double.longBitsToDouble(value);
    }

    public static void readBytes(final byte[] bytes, int offset, final byte[] dst) {
        System.arraycopy(bytes, offset, dst, 0, dst.length);
    }

    public static String readString(final byte[] bytes, final int offset, final int length) {
        final char[] chars = new char[length];
        final int end = offset + length;
        for (int i = 0, j = offset; i < length; i++) {
            chars[i] = (char) (((bytes[j++] & 0xFF)) | ((bytes[j++] & 0xFF) << 8));
        }
        return new String(chars);
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
