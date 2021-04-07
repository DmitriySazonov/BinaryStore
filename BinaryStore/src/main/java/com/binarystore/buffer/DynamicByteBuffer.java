package com.binarystore.buffer;

import java.util.Arrays;

public class DynamicByteBuffer extends BaseByteBuffer {

    public DynamicByteBuffer(int initialSize) {
        super(new byte[initialSize], 0, initialSize - 1);
    }

    public DynamicByteBuffer(byte[] bytes) {
        super(bytes, 0, bytes.length - 1);
    }

    protected void checkFreeSpace(int needSpace) {
        boolean hasFreeSpace = bytes.length - offset > needSpace;
        if (hasFreeSpace) return;
        bytes = Arrays.copyOf(bytes, bytes.length * 2);
        setSource(bytes, 0, bytes.length - 1);
    }
}
