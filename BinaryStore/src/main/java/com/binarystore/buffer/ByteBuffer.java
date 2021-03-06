package com.binarystore.buffer;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;

public interface ByteBuffer {

    final class Meta {
        int maxCharBufferLength = 0;
        @Nonnull
        char[] charBuffer;

        public Meta(@Nonnull char[] initialCharBuffer) {
            charBuffer = initialCharBuffer;
        }

        public final void ensureCharBufferLength(final int length) {
            if (maxCharBufferLength < length) {
                maxCharBufferLength = length;
            }
            if (charBuffer.length < length) {
                charBuffer = new char[maxCharBufferLength];
            }
        }
    }

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

    StaticByteBuffer getSubBuffer(int start, int end);

    int getAbsoluteOffset();

    int getOffset();

    int getSize();

    void setOffset(final int offset);

    void moveOffset(final int offset);

    void write(final char value);

    void write(final boolean value);

    void write(final byte value);

    void write(final short value);

    void write(final int value);

    void write(final long value);

    void write(final float value);

    void write(final double value);

    void write(final byte[] value);

    void write(@Nonnull final String value);

    void write(@Nonnull final StaticByteBuffer value);

    char readChar();

    byte readByte();

    boolean readBoolean();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    void readBytes(final byte[] dst);

    String readString(final int length);


    @Nonnull
    OutputStream reserveOutputStream(final int size);

    @Nonnull
    InputStream reserveInputStream(final int size);
}
