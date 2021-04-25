package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.ByteBufferHelper;

import java.util.Objects;

import javax.annotation.CheckForNull;

public abstract class Key<T extends Key<?>> implements Comparable<T> {

    private final static byte STRING = 1;
    private final static byte INT = 2;

    public static Key<?> read(ByteBuffer byteBuffer) throws IllegalArgumentException {
        final byte keyType = byteBuffer.readByte();
        if (keyType == STRING) {
            final int length = byteBuffer.readInt();
            return new String(byteBuffer.readString(length));
        } else if (keyType == INT) {
            return new Int(byteBuffer.readInt());
        } else {
            throw new IllegalArgumentException("Unknown id of key - " + keyType);
        }
    }

    public abstract int getSize();

    public abstract void saveTo(ByteBuffer byteBuffer) throws Exception;

    @Override
    public int compareTo(@CheckForNull T key) {
        if (key == null || key instanceof Int && this instanceof String) {
            return 1;
        }
        if (key instanceof String && this instanceof Int) {
            return -1;
        }
        return 0;
    }

    public static class String extends Key<String> {

        public final java.lang.String value;
        private final int size;

        public String(java.lang.String value) {
            this.value = value;
            this.size = ByteBuffer.BYTE_BYTES +
                    ByteBuffer.INTEGER_BYTES +
                    ByteBufferHelper.getSize(value);
        }

        @Override
        public int compareTo(@CheckForNull String key) {
            final int compare = super.compareTo(key);
            return compare == 0 && key != null ? value.compareTo(key.value) : compare;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final String key = (String) obj;
            return Objects.equals(value, key.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public java.lang.String toString() {
            return "String{value='" + value + '}';
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public void saveTo(ByteBuffer byteBuffer) {
            byteBuffer.write(STRING);
            byteBuffer.write(value.length());
            byteBuffer.write(value);
        }
    }

    public static class Int extends Key<Int> {

        public final int value;
        private final int size;

        public Int(int value) {
            this.value = value;
            this.size = ByteBuffer.BYTE_BYTES +
                    ByteBuffer.INTEGER_BYTES;
        }

        @Override
        public int compareTo(@CheckForNull Int key) {
            final int compare = super.compareTo(key);
            return compare == 0 && key != null ? Integer.compare(value, key.value) : compare;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final Int key = (Int) obj;
            return value == key.value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Int{value=" + value + '}';
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public void saveTo(ByteBuffer byteBuffer) {
            byteBuffer.write(INT);
            byteBuffer.write(value);
        }
    }
}
