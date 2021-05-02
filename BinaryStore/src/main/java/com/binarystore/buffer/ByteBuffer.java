package com.binarystore.buffer;

public interface ByteBuffer {

    byte TRUE = 1;
    byte FALSE = 0;

    int BOOLEAN_BYTES = 1;
    int BYTE_BYTES = 1;
    int CHAR_BYTES = 2;
    int SHORT_BYTES = 2;
    int INTEGER_BYTES = 4;
    int LONG_BYTES = 8;
    int FLOAT_BYTES = 4;
    int DOUBLE_BYTES = 8;

    int getOffset();

    int getSize();

    void setOffset(final int offset);

    void moveOffset(final int offset);

    void write(final boolean value);

    void write(final byte value);

    void write(final short value);

    void write(final int value);

    void write(final long value);

    void write(final float value);

    void write(final double value);

    void write(final byte[] value);

    void write(final String value);

    byte readByte();

    boolean readBoolean();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    void readBytes(final byte[] dst);

    String readString(final int length);
}
