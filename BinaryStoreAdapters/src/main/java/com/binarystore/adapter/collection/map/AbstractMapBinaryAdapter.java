package com.binarystore.adapter.collection.map;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
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
        BinaryAdapter<Object> lastKeyAdapter = null;
        BinaryAdapter<Object> lastValueAdapter = null;

        private Class<?> lastKeyClass = null;
        private Class<?> lastValueClass = null;

        void setKeyClass(@CheckForNull Object key) throws Exception {
            final Class<?> keyClass = key != null ? key.getClass() : null;
            if (keyClass != lastKeyClass) {
                lastKeyClass = keyClass;
                lastKeyAdapter = getAdapterForClass(keyClass);
            }
        }

        void setValueClass(@CheckForNull Object value) throws Exception {
            final Class<?> valueClass = value != null ? value.getClass() : null;
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

    @Nonnull
    private final MapSettings settings;
    @Nonnull
    private final BinaryAdapterProvider adapterProvider;

    protected AbstractMapBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @CheckForNull final MapSettings settings
    ) {
        this.adapterProvider = provider;
        this.settings = settings != null ? settings : MapSettings.defaultSettings;
    }

    @Nonnull
    protected abstract T createMap(int size);

    @Override
    @SuppressWarnings("unchecked")
    public int getSize(@Nonnull T value) throws Exception {
        final Adapters adapters = new Adapters();
        int accumulator = key().getSize();
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            adapters.setKeyClass(entry.getKey());
            adapters.setValueClass(entry.getValue());
            accumulator += adapters.lastKeyAdapter.key().getSize();
            accumulator += adapters.lastValueAdapter.key().getSize();
            accumulator += adapters.lastKeyAdapter.getSize(entry.getKey());
            accumulator += adapters.lastValueAdapter.getSize(entry.getValue());
        }
        return ByteBuffer.INTEGER_BYTES // map size
                + ByteBuffer.INTEGER_BYTES * value.size() // entries offset meta
                + accumulator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        int index = 0;
        final Adapters adapters = new Adapters();
        final int[] offsets = new int[value.size()];
        byteBuffer.write(value.size());
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(value.size() * ByteBuffer.INTEGER_BYTES);
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            offsets[index++] = byteBuffer.getOffset();
            adapters.setKeyClass(entry.getKey());
            adapters.setValueClass(entry.getValue());
            adapters.lastKeyAdapter.key().saveTo(byteBuffer);
            adapters.lastValueAdapter.key().saveTo(byteBuffer);
            adapters.lastKeyAdapter.serialize(byteBuffer, entry.getKey());
            adapters.lastValueAdapter.serialize(byteBuffer, entry.getValue());
        }
        final int endOffset = byteBuffer.getOffset();
        byteBuffer.setOffset(startOffset);
        for (int offset : offsets) {
            byteBuffer.write(offset);
        }
        byteBuffer.setOffset(endOffset);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Adapters adapters = new Adapters();
        final int size = byteBuffer.readInt();
        final int startDataOffset = byteBuffer.readInt();
        final T map = createMap(size);
        final Map<Object, Object> mutableMap = (Map<Object, Object>) map;
        byteBuffer.setOffset(startDataOffset);
        for (int i = 0; i < size; i++) {
            adapters.setKeyKey(Key.read(byteBuffer));
            adapters.setValueKey(Key.read(byteBuffer));
            mutableMap.put(
                    adapters.lastKeyAdapter.deserialize(byteBuffer),
                    adapters.lastValueAdapter.deserialize(byteBuffer)
            );
        }
        return map;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private BinaryAdapter<Object> getAdapterForKey(@Nonnull Key<?> key) throws Exception {
        final BinaryAdapter<?> adapter;
        if (key.equals(NullBinaryAdapter.instance.key())) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterByKey(key, null);
        }
        if (adapter == null) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key);
        }
        return (BinaryAdapter<Object>) adapter;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private BinaryAdapter<Object> getAdapterForClass(@CheckForNull Class<?> clazz) throws Exception {
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
