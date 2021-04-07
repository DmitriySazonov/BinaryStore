package com.binarystore.buffer;

public interface ByteBuffer {

    int BOOLEAN_BYTES = 1;
    int BYTE_BYTES = 1;
    int SHORT_BYTES = 2;
    int INTEGER_BYTES = 4;
    int LONG_BYTES = 8;
    int FLOAT_BYTES = 4;
    int DOUBLE_BYTES = 8;

    int getOffset();

    int getSize();

    void setOffset(int offset);

    void moveOffset(int offset);

    void write(boolean value);

    void write(byte value);

    void write(short value);

    void write(int value);

    void write(long value);

    void write(float value);

    void write(double value);

    void write(final byte[] value);

    byte readByte();

    boolean readBoolean();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    void readBytes(byte[] dst);
}
