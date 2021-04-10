package com.binarystore.adapter.boxed;

import com.binarystore.adapter.AbstractAdapterFactory;
import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BaseBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public class IntBinaryAdapter extends BaseBinaryAdapter<Integer> {

    private static final int ID = DefaultAdapters.INT;

    private static final int NULL_SIZE = ByteBuffer.BOOLEAN_BYTES;
    private static final int FULL_SIZE = NULL_SIZE + ByteBuffer.INTEGER_BYTES;
    public static final AdapterFactory<Integer> factory = new AbstractAdapterFactory<Integer>(ID) {
        @Override
        @Nonnull
        public BinaryAdapter<Integer> create(@Nonnull Context context) {
            return new IntBinaryAdapter();
        }
    };

    @Override
    public int id() {
        return ID;
    }

    @Override
    public int getSize(Integer value) throws Exception {
        return value == null ? NULL_SIZE : FULL_SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer, Integer value) throws Exception {
        if (value != null) {
            byteBuffer.write(true);
            byteBuffer.write(value);
        } else {
            byteBuffer.write(false);
        }
    }

    @Override
    public Integer deserialize(ByteBuffer byteBuffer) throws Exception {
        return byteBuffer.readBoolean() ? byteBuffer.readInt() : null;
    }
}
