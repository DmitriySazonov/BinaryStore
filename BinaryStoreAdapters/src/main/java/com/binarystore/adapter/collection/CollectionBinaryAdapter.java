package com.binarystore.adapter.collection;

import com.binarystore.VersionException;
import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.map.AbstractMapBinaryAdapter;
import com.binarystore.buffer.ByteBuffer;

import java.util.Collection;
import java.util.Map;

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
            @CheckForNull final CollectionSettings settings
    ) {
        this.adapterProvider = provider;
        this.settings = settings != null ? settings : CollectionSettings.defaultSettings;
    }

    protected abstract T createCollection(int size);

    @Override
    public int getSize(@Nonnull T value) throws Exception {
        final Adapters adapters = new Adapters();
        int accumulator = key().getSize();
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
                    throw new IllegalStateException("Fail getSize for class" + element.getClass());
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
        final byte version = byteBuffer.readByte();
        if (this.version != version) {
            throw new VersionException(this.version, version);
        }
        final int size = byteBuffer.readInt();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // skip offest to meta
        final T collection = createCollection(size);
        final Collection<Object> mutableCollection = (Collection<Object>) collection;
        for (int i = 0; i < size; i++) {
            final Key valueKey = Key.read(byteBuffer);
            adapters.setValueKey(valueKey);
            if (checkForNull(adapters.lastValueAdapter, valueKey)) {
                continue;
            }
            mutableCollection.add(adapters.lastValueAdapter.deserialize(byteBuffer));
        }
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return collection;
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
