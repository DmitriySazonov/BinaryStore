package com.binarystore.adapter.collection.map;

import com.binarystore.VersionException;
import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.collection.map.MapSettings.ItemStrategy;
import com.binarystore.buffer.ByteBuffer;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class AbstractMapBinaryAdapter<T extends Map> extends AbstractBinaryAdapter<T> {

    private class Adapters {
        Key<?> lastKeyKey = null;
        Key<?> lastValueKey = null;
        @CheckForNull
        BinaryAdapter<Object> lastKeyAdapter = null;
        @CheckForNull
        BinaryAdapter<Object> lastValueAdapter = null;

        private Class<?> lastKeyClass = null;
        private Class<?> lastValueClass = null;

        void setKeyClass(@CheckForNull Object key) throws Exception {
            final Class<?> keyClass = key != null ? key.getClass() : NullBinaryAdapter.NULL_CLASS;
            if (keyClass != lastKeyClass) {
                lastKeyClass = keyClass;
                lastKeyAdapter = getAdapterForClass(keyClass);
            }
        }

        void setValueClass(@CheckForNull Object value) throws Exception {
            final Class<?> valueClass = value != null ? value.getClass() : NullBinaryAdapter.NULL_CLASS;
            if (valueClass != lastValueClass) {
                lastValueClass = valueClass;
                lastValueAdapter = getAdapterForClass(valueClass);
            }
        }

        void setKeyKey(Key<?> keyClass) throws Exception {
            if (!keyClass.equals(lastKeyKey)) {
                lastKeyKey = keyClass;
                lastKeyAdapter = getAdapterForKey(keyClass);
            }
        }

        void setValueKey(Key<?> valueClass) throws Exception {
            if (!valueClass.equals(lastValueKey)) {
                lastValueKey = valueClass;
                lastValueAdapter = getAdapterForKey(valueClass);
            }
        }
    }

    private final byte version = 1;

    @Nonnull
    private final MapSettings settings;
    @Nonnull
    private final BinaryAdapterProvider adapterProvider;

    protected AbstractMapBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final MapSettings settings
    ) {
        this.adapterProvider = provider;
        this.settings = settings;
    }

    /**
     * Scheme
     * version - 1 byte
     * written item count - 4 byte // may not be equals map.size
     * additional meta - n byte // see in heirs
     * offset to meta begin - 4 byte
     * data  - sum of items key + value
     * key_1|value_1
     * ...
     * key_n|value_n
     * meta - 4 byte * (written item count)
     * offset_for_item_1
     * ...
     * offset_for_item_n
     */

    @Nonnull
    protected abstract T createMap(int size, @Nonnull ByteBuffer buffer) throws Exception;

    protected int getSizeAdditionalMeta(@Nonnull T value) throws Exception {
        return 0;
    }

    protected void serializeAdditionalMeta(@Nonnull ByteBuffer buffer, @Nonnull T value) throws Exception {

    }

    @Override
    @SuppressWarnings("unchecked")
    public final int getSize(@Nonnull T value) throws Exception {
        final Adapters adapters = new Adapters();
        final Set<Map.Entry> entries = value.entrySet();
        int actualSize = 0;
        int accumulator = 0;
        for (Map.Entry entry : entries) {
            final Object entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapters.setKeyClass(entryKey);
            adapters.setValueClass(entryValue);
            if (checkForNull(adapters.lastKeyAdapter, entryKey)) {
                continue;
            }
            if (checkForNull(adapters.lastValueAdapter, entryValue)) {
                continue;
            }
            int itemSize = 0;
            try {
                itemSize += adapters.lastKeyAdapter.key().getSize();
                itemSize += adapters.lastValueAdapter.key().getSize();
                itemSize += adapters.lastKeyAdapter.getSize(entryKey);
                itemSize += adapters.lastValueAdapter.getSize(entryValue);
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == ItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for key " + entryKey);
                }
                continue;
            }
            actualSize++;
            accumulator += itemSize;
        }
        return (ByteBuffer.BYTE_BYTES +  // version
                ByteBuffer.INTEGER_BYTES +  // map size
                getSizeAdditionalMeta(value) + // additional meta for heir
                ByteBuffer.INTEGER_BYTES +  // offset to meta
                accumulator +  // data size
                ByteBuffer.INTEGER_BYTES * actualSize // entries offsets
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        int index = 0;
        final Adapters adapters = new Adapters();
        final int[] offsets = new int[value.size()];
        byteBuffer.write(version);
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        serializeAdditionalMeta(byteBuffer, value); // see in heir
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            final int offset = byteBuffer.getOffset();
            final Object entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapters.setKeyClass(entryKey);
            adapters.setValueClass(entryValue);
            if (checkForNull(adapters.lastKeyAdapter, entryKey)) {
                continue;
            }
            if (checkForNull(adapters.lastValueAdapter, entryValue)) {
                continue;
            }

            try {
                adapters.lastKeyAdapter.key().saveTo(byteBuffer);
                adapters.lastValueAdapter.key().saveTo(byteBuffer);
                adapters.lastKeyAdapter.serialize(byteBuffer, entryKey);
                adapters.lastValueAdapter.serialize(byteBuffer, entryValue);
                offsets[index++] = offset;
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == ItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail serialization for key " + entryKey);
                }
                byteBuffer.setOffset(offset);
            }
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

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public final T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Adapters adapters = new Adapters();
        final byte version = byteBuffer.readByte();
        if (this.version != version) {
            throw new VersionException(this.version, version);
        }
        final int size = byteBuffer.readInt();
        final T map = createMap(size, byteBuffer);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // skip offest to meta
        final Map<Object, Object> mutableMap = (Map<Object, Object>) map;
        for (int i = 0; i < size; i++) {
            final Key entryKey = Key.read(byteBuffer);
            final Key valueKey = Key.read(byteBuffer);
            adapters.setKeyKey(entryKey);
            adapters.setValueKey(valueKey);
            if (checkForNull(adapters.lastKeyAdapter, entryKey)) {
                continue;
            }
            if (checkForNull(adapters.lastValueAdapter, valueKey)) {
                continue;
            }
            mutableMap.put(
                    adapters.lastKeyAdapter.deserialize(byteBuffer),
                    adapters.lastValueAdapter.deserialize(byteBuffer)
            );
        }
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return map;
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Object key) {
        if (adapter == null && settings.unknownItemStrategy == ItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key.getClass());
        }
        return adapter == null;
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Key<?> key) {
        if (adapter == null && settings.unknownItemStrategy == ItemStrategy.THROW_EXCEPTION) {
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
