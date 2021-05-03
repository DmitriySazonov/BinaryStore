package com.binarystore.adapter.collection.iterable.base;

import com.binarystore.adapter.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

public abstract class IterableBinaryAdapter<T extends Iterable<?>> extends AbstractCollectionBinaryAdapter<T> {

    private final BinaryAdapterProvider adapterProvider;

    protected class Adapters {
        Class<?> lastValueClass = null;
        BinaryAdapter<Object> lastValueAdapter = null;

        void setValueClass(Class<?> valueClass) throws Exception {
            if (valueClass != lastValueClass) {
                lastValueClass = valueClass;
                lastValueAdapter = getAdapterForClass(valueClass);
            }
        }
    }

    protected IterableBinaryAdapter(final BinaryAdapterProvider provider) {
        adapterProvider = provider;
    }

    abstract  int getCollectionSize();

    @Override
    public int getSize(@Nonnull T value) throws Exception {
        final Adapters adapters = new Adapters();
        int accumulator = key().getSize();
        int elementCount = 0;
        for (Object element : value) {
            adapters.setValueClass(element.getClass());
            accumulator += adapters.lastValueAdapter.key().getSize();
            elementCount++;
        }
        return ByteBuffer.INTEGER_BYTES // collection size
                + ByteBuffer.INTEGER_BYTES * elementCount // entries offset meta
                + accumulator;
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        int index = 0;
        int collectionSize = getCollectionSize();
        final Adapters adapters = new Adapters();
        final int[] offsets = new int[collectionSize];
        byteBuffer.write(collectionSize);
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(collectionSize * ByteBuffer.INTEGER_BYTES);
        for (Object element : value) {
            offsets[index++] = byteBuffer.getOffset();
            adapters.setValueClass(element.getClass());
            adapters.lastValueAdapter.key().saveTo(byteBuffer);
            adapters.lastValueAdapter.serialize(byteBuffer, element);
        }
        final int endOffset = byteBuffer.getOffset();
        byteBuffer.setOffset(startOffset);
        for (int offset : offsets) {
            byteBuffer.write(offset);
        }
        byteBuffer.setOffset(endOffset);
    }


    @Nonnull
    @SuppressWarnings("unchecked")
    protected BinaryAdapter<Object> getAdapterForKey(Key<?> key) throws Exception {
        final BinaryAdapter<?> adapter = adapterProvider.getAdapterByKey(key);
        if (adapter == null) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key);
        }
        return (BinaryAdapter<Object>) adapter;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected BinaryAdapter<Object> getAdapterForClass(Class<?> clazz) throws Exception {
        final BinaryAdapter<?> adapter = adapterProvider.getAdapterForClass(clazz);
        if (adapter == null) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + clazz);
        }
        return (BinaryAdapter<Object>) adapter;
    }
}
