package com.binarystore.adapter.collection;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.collection.serialization.CollectionBinaryDeserializerV1;
import com.binarystore.adapter.collection.serialization.CollectionBinarySerializer;
import com.binarystore.buffer.ByteBuffer;

import java.util.Collection;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class AbstractCollectionBinaryAdapter<T extends Collection> extends AbstractBinaryAdapter<T> {

    private final CollectionBinarySerializer<T> serializer;
    private final CollectionBinaryDeserializerV1<T> deserializer;

    protected AbstractCollectionBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final CollectionSettings settings
    ) {
        this.serializer = new CollectionBinarySerializer<>(provider, settings);
        this.deserializer = new CollectionBinaryDeserializerV1<T>(provider, settings) {
            @Override
            public T createCollection(int size) {
                return AbstractCollectionBinaryAdapter.this.createCollection(size);
            }
        };
    }

    protected abstract T createCollection(int size);

    @Override
    public int getSize(@Nonnull T value) throws Exception {
        return serializer.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        serializer.serialize(byteBuffer, value);
    }

    @Nonnull
    @Override
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return deserializer.deserialize(byteBuffer);
    }

    /*@Nonnull
    @SuppressWarnings("unchecked")
    protected T deserializeSubCollection(@Nonnull ByteBuffer byteBuffer, int startIndex, int endIndex) throws Exception {
        final AdapterHelper adapters = new AdapterHelper(adapterProvider);
        CollectionAdapterUtils.checkVersion(byteBuffer, version);
        final int size = byteBuffer.readInt();
        CollectionAdapterUtils.checkSubCollectionBounds(startIndex, endIndex, size);

        final int metaOffset = byteBuffer.readInt();  // find meta start index
        byteBuffer.setOffset(metaOffset); // move to meta
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * startIndex); // find element index in byte buffer
        final int elementOffset = byteBuffer.readInt();
        byteBuffer.setOffset(elementOffset); // move to element

        final int subCollectionSize = endIndex - startIndex + 1;

        final T collection = createCollection(subCollectionSize);
        final Collection<Object> mutableCollection = (Collection<Object>) collection;
        for (int i = 0; i < subCollectionSize; i++) {
            mutableCollection.add(deserializeElement(byteBuffer, adapters));
        }
        byteBuffer.setOffset(metaOffset);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return collection;
    }

    @CheckForNull
    protected Object deserializeElementAt(@Nonnull ByteBuffer byteBuffer, int index) throws Exception {
        final AdapterHelper adapters = new AdapterHelper(adapterProvider);
        CollectionAdapterUtils.checkVersion(byteBuffer, version);
        final int size = byteBuffer.readInt();
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Illegal Capacity: " + index);
        }

        final int metaOffset = byteBuffer.readInt();  // find meta start index
        byteBuffer.setOffset(metaOffset); // move to meta
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * index); // find element index in byte buffer
        final int elementOffset = byteBuffer.readInt();
        byteBuffer.setOffset(elementOffset); // move to element

        final Object result = deserializeElement(byteBuffer, adapters); // read element

        byteBuffer.setOffset(metaOffset);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return result;
    }*/
}
