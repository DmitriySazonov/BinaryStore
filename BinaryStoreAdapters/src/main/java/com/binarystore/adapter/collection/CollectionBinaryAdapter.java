package com.binarystore.adapter.collection;

import com.binarystore.VersionException;
import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.buffer.ByteBuffer;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class CollectionBinaryAdapter<T extends Collection> extends AbstractBinaryAdapter<T> {

    @Nonnull
    private final CollectionSettings settings;
    private final BinaryAdapterProvider adapterProvider;
    private final byte version = 1;

    protected class Adapters {
        Key<?> lastValueKey = null;
        Class<?> lastValueClass = null;
        BinaryAdapter<Object> lastValueAdapter = null;

        void setValueClass(@CheckForNull Object value) throws Exception {
            final Class<?> valueClass = value != null ? value.getClass() : NullBinaryAdapter.NULL_CLASS;
            if (valueClass != lastValueClass) {
                lastValueClass = valueClass;
                lastValueAdapter = getAdapterForClass(valueClass);
            }
        }

        void setValueKey(Key<?> valueClass) throws Exception {
            if (!valueClass.equals(lastValueKey)) {
                lastValueKey = valueClass;
                lastValueAdapter = getAdapterForKey(valueClass);
            }
        }
    }

    protected CollectionBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final CollectionSettings settings
    ) {
        this.adapterProvider = provider;
        this.settings = settings;
    }

    protected abstract T createCollection(int size);

    @Override
    public int getSize(@Nonnull T value) throws Exception {
        final Adapters adapters = new Adapters();
        int accumulator = 0;
        int elementCount = 0;
        for (Object element : value) {
            adapters.setValueClass(element);
            if (checkForNull(adapters.lastValueAdapter, element)) {
                continue;
            }
            int itemSize = 0;
            try {
                itemSize += adapters.lastValueAdapter.key().getSize();
                itemSize += adapters.lastValueAdapter.getSize(element);
            } catch  (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for class" + element.getClass());
                }
                continue;
            }
            accumulator += itemSize;
            elementCount++;
        }
        return (ByteBuffer.BYTE_BYTES +  // version
                ByteBuffer.INTEGER_BYTES + // collection size
                ByteBuffer.INTEGER_BYTES +  // offset to meta
                accumulator + // data size
                ByteBuffer.INTEGER_BYTES * elementCount // collection offsets
        );
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        int index = 0;
        final Adapters adapters = new Adapters();
        final int[] offsets = new int[value.size()];
        byteBuffer.write(version);
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta
        for (Object element : value) {
            adapters.setValueClass(element);
            if (checkForNull(adapters.lastValueAdapter, element)) {
                continue;
            }
            final int offset = byteBuffer.getOffset();
            try {
                adapters.lastValueAdapter.key().saveTo(byteBuffer);
                adapters.lastValueAdapter.serialize(byteBuffer, element);
                offsets[index++] = offset;
            } catch  (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail serialization for class" + element.getClass());
                }
                byteBuffer.setOffset(offset);
            }
        }
        final int endDataOffset = byteBuffer.getOffset();
        byteBuffer.setOffset(startOffset);
        byteBuffer.write(index); // write actual size of collection
        byteBuffer.write(endDataOffset); // write offset to start of meta
        byteBuffer.setOffset(endDataOffset); // move to the end to write meta
        for (int i = 0; i < index; i++) {
            byteBuffer.write(offsets[i]);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Adapters adapters = new Adapters();
        checkVersion(byteBuffer);
        final int size = byteBuffer.readInt();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // skip offest to meta
        final T collection = createCollection(size);
        final Collection<Object> mutableCollection = (Collection<Object>) collection;
        for (int i = 0; i < size; i++) {
            mutableCollection.add(deserializeElement(byteBuffer, adapters));
        }
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return collection;
    }

    @SuppressWarnings("unchecked")
    protected T deserializeSubCollection(@Nonnull ByteBuffer byteBuffer, int startIndex, int endIndex) throws Exception {
        final Adapters adapters = new Adapters();
        checkVersion(byteBuffer);
        final int size = byteBuffer.readInt();
        checkSubCollectionBounds(startIndex, endIndex, size);

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

    protected Object deserializeElementAt(@Nonnull ByteBuffer byteBuffer, int index) throws Exception {
        final Adapters adapters = new Adapters();
        checkVersion(byteBuffer);
        final int size = byteBuffer.readInt();
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Illegal Capacity: " + index);
        }

        final int metaOffset = byteBuffer.readInt();  // find meta start index
        byteBuffer.setOffset(metaOffset); // move to meta
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * index); // find element index in byte buffer
        final int elementOffset = byteBuffer.readInt();
        byteBuffer.setOffset(elementOffset); // move to element

        Object result = deserializeElement(byteBuffer, adapters); // read element

        byteBuffer.setOffset(metaOffset);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return result;
    }

    private Object deserializeElement(@Nonnull ByteBuffer byteBuffer, Adapters adapters) throws Exception {
        final Key valueKey = Key.read(byteBuffer);
        adapters.setValueKey(valueKey);
        Object result = null;
        try {
            if (!checkForNull(adapters.lastValueAdapter, valueKey)) {
                result = adapters.lastValueAdapter.deserialize(byteBuffer);
            }
        } catch (Throwable throwable) {
            if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                throw new IllegalStateException("Fail deserialize for key " + valueKey);
            }
        }
        return result;
    }

    private void checkVersion(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final byte version = byteBuffer.readByte();
        if (this.version != version) {
            throw new VersionException(this.version, version);
        }
    }

    private void checkSubCollectionBounds(int startIndex, int endIndex, int collectionSize) throws Exception {
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + startIndex);
        } else if (endIndex > collectionSize) {
            throw new IndexOutOfBoundsException("toIndex = " + endIndex);
        } else if (startIndex > endIndex) {
            throw new IllegalArgumentException("fromIndex(" + startIndex + ") > toIndex(" + endIndex + ")");
        }
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Object key) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key.getClass());
        }
        return adapter == null;
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Key<?> key) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key);
        }
        return adapter == null;
    }

    @SuppressWarnings("unchecked")
    private BinaryAdapter<Object> getAdapterForKey(@Nonnull Key<?> key) throws Exception {
        final BinaryAdapter<?> adapter;
        if (key.equals(NullBinaryAdapter.instance.key())) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterByKey(key, null);
        }
        return (BinaryAdapter<Object>) adapter;
    }

    @SuppressWarnings("unchecked")
    private BinaryAdapter<Object> getAdapterForClass(@Nonnull Class<?> clazz) throws Exception {
        final BinaryAdapter<?> adapter;
        if (clazz == NullBinaryAdapter.NULL_CLASS) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterForClass(clazz, null);
        }
        return (BinaryAdapter<Object>) adapter;
    }
}
