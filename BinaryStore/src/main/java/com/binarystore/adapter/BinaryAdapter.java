package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

public interface BinaryAdapter<T> {

    interface Key extends Comparable<Key> {
        void saveTo(ByteBuffer byteBuffer);
    }

    class StringKey implements Key {

        @Override
        public void saveTo(ByteBuffer byteBuffer) {

        }

        @Override
        public int compareTo(Key key) {
            return 0;
        }
    }

    class IntKey implements Key {

        @Override
        public void saveTo(ByteBuffer byteBuffer) {

        }

        @Override
        public int compareTo(Key key) {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    int id();

    int getSize(T value) throws Exception;

    void serialize(ByteBuffer byteBuffer, T value) throws Exception;

    T deserialize(ByteBuffer byteBuffer) throws Exception;
}
