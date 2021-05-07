package com.binarystore.adapter.collection;

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

    protected class Adapters {
        Class<?> lastValueClass = null;
        BinaryAdapter<Object> lastValueAdapter = null;

        void setValueClass(@CheckForNull Object value) throws Exception {
            final Class<?> valueClass = value != null ? value.getClass() : null;
            if (valueClass != lastValueClass) {
                lastValueClass = valueClass;
                lastValueAdapter = getAdapterForClass(valueClass);
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
            if (checkForNull(adapters.lastValueAdapter, value)) {
                continue;
            }
            try {
                adapters.setValueClass(element.getClass());
            } catch  (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for class" + element.getClass());
                }
                continue;
            }
            accumulator += adapters.lastValueAdapter.key().getSize();
            elementCount++;
        }
        return (ByteBuffer.BYTE_BYTES +  // version
                ByteBuffer.INTEGER_BYTES + // collection size
                getSizeAdditionalMeta(value) + // additional meta for heir
                ByteBuffer.INTEGER_BYTES +  // offset to meta
                accumulator + // data size
                ByteBuffer.INTEGER_BYTES * elementCount // entries offsets
        );
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        int index = 0;
        int collectionSize = value.size();
        final Adapters adapters = new Adapters();
        final int[] offsets = new int[collectionSize];
        byteBuffer.write(collectionSize);
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta
        serializeAdditionalMeta(byteBuffer, value); // see in heir
        for (Object element : value) {
            offsets[index++] = byteBuffer.getOffset();
            if (checkForNull(adapters.lastValueAdapter, value)) {
                continue;
            }
            try {
                adapters.setValueClass(element.getClass());
            } catch  (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for class" + element.getClass());
                }
                continue;
            }
            adapters.lastValueAdapter.key().saveTo(byteBuffer);
            adapters.lastValueAdapter.serialize(byteBuffer, element);
        }
        final int endDataOffset = byteBuffer.getOffset();
        byteBuffer.setOffset(startOffset);
        byteBuffer.write(index); // write actual size of map
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
        final int size = byteBuffer.readInt();
        final int startDataOffset = byteBuffer.readInt();
        final T collection = createCollection(size);
        final Collection<Object> mutableCollection = (Collection<Object>) collection;
        byteBuffer.setOffset(startDataOffset);
        for (int i = 0; i < size; i++) {
            final Key valueKey = Key.read(byteBuffer);
            adapters.setValueClass(valueKey);
            if (checkForNull(adapters.lastValueAdapter, valueKey)) {
                continue;
            }
            mutableCollection.add(adapters.lastValueAdapter.deserialize(byteBuffer));
        }
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return collection;
    }

    protected int getSizeAdditionalMeta(@Nonnull T value) throws Exception {
        return 0;
    }

    protected void serializeAdditionalMeta(@Nonnull ByteBuffer buffer, @Nonnull T value) throws Exception {

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

    @Nonnull
    @SuppressWarnings("unchecked")
    protected BinaryAdapter<Object> getAdapterForClass(Class<?> clazz) throws Exception {
        final BinaryAdapter<?> adapter;
        if (clazz == null) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterForClass(clazz, null);
        }
        if (adapter == null) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + clazz);
        }
        return (BinaryAdapter<Object>) adapter;
    }
}
