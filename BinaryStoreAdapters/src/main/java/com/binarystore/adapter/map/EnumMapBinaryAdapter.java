package com.binarystore.adapter.map;

import com.binarystore.VersionException;
import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.EnumBinaryAdapter;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.buffer.ByteBuffer;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public final class EnumMapBinaryAdapter extends AbstractBinaryAdapter<EnumMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ENUM_MAP;
    private static final byte version = 1;
    private static Field keyTypeField = null;

    private class Adapter {
        @CheckForNull
        BinaryAdapter<Object> lastValueAdapter = null;

        private Key<?> lastValueKey = null;
        private Class<?> lastValueClass = null;


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

    @Nonnull
    private final MapSettings settings;
    @Nonnull
    private final BinaryAdapterProvider adapterProvider;
    @Nonnull
    private final BinaryAdapter<String> stringBinaryAdapter;
    @Nonnull
    private final BinaryAdapter<Class> classBinaryAdapter;

    protected EnumMapBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final MapSettings settings
    ) throws Exception {
        this.adapterProvider = provider;
        this.settings = settings;
        this.stringBinaryAdapter = Objects.requireNonNull(provider
                .getAdapterForClass(String.class, null));
        this.classBinaryAdapter = Objects.requireNonNull(provider
                .getAdapterForClass(Class.class, null));
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    /**
     * Scheme
     * version - 1 byte
     * adapter key - adapter.key().getSize()
     * enum class - classAdapter.getSize(enumClass)
     * written item count - 4 byte // may not be equals map.size
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

    @Override
    @SuppressWarnings("unchecked")
    public final int getSize(@Nonnull EnumMap value) throws Exception {
        final Adapter adapter = new Adapter();
        final Class<Enum> enumClass = extractEnumClass(value);
        final BinaryAdapter<Enum> keyAdapter = getAdapterForMapKey(enumClass);
        final Set<Map.Entry> entries = value.entrySet();
        int actualSize = 0;
        int accumulator = 0;
        for (Map.Entry<Enum, Object> entry : entries) {
            final Enum entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapter.setValueClass(entryValue);
            if (checkForNull(adapter.lastValueAdapter, entryValue)) {
                continue;
            }
            int itemSize = 0;
            try {
                itemSize += adapter.lastValueAdapter.key().getSize();
                itemSize += keyAdapter.getSize(entryKey);
                itemSize += adapter.lastValueAdapter.getSize(entryValue);
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == MapSettings.ItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for key " + entryKey);
                }
                continue;
            }
            actualSize++;
            accumulator += itemSize;
        }
        return (ByteBuffer.BYTE_BYTES +  // version
                keyAdapter.key().getSize() + // adapter key
                classBinaryAdapter.getSize(enumClass) + // enum class
                ByteBuffer.INTEGER_BYTES +  // map size
                ByteBuffer.INTEGER_BYTES +  // offset to meta
                accumulator +  // data size
                ByteBuffer.INTEGER_BYTES * actualSize // entries offsets
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull EnumMap value) throws Exception {
        int index = 0;
        final Adapter adapter = new Adapter();
        final Class<Enum> enumClass = extractEnumClass(value);
        final BinaryAdapter<Enum> keyAdapter = getAdapterForMapKey(enumClass);
        final int[] offsets = new int[value.size()];
        byteBuffer.write(version);
        keyAdapter.key().saveTo(byteBuffer);
        classBinaryAdapter.serialize(byteBuffer, enumClass);
        final int startOffset = byteBuffer.getOffset();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry<Enum, Object> entry : entries) {
            final int offset = byteBuffer.getOffset();
            final Enum entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapter.setValueClass(entryValue);
            if (checkForNull(adapter.lastValueAdapter, entryValue)) {
                continue;
            }

            try {
                adapter.lastValueAdapter.key().saveTo(byteBuffer);

                keyAdapter.serialize(byteBuffer, entryKey);
                adapter.lastValueAdapter.serialize(byteBuffer, entryValue);

                offsets[index++] = offset;
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == MapSettings.ItemStrategy.THROW_EXCEPTION) {
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
    public final EnumMap deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final byte version = byteBuffer.readByte();
        if (EnumMapBinaryAdapter.version != version) {
            throw new VersionException(EnumMapBinaryAdapter.version, version);
        }
        final Adapter adapter = new Adapter();
        final Key<?> key = Key.read(byteBuffer);
        final Class<Enum> enumClass = classBinaryAdapter.deserialize(byteBuffer);
        final BinaryAdapter<Enum> keyAdapter = getAdapterForMapKey(key, enumClass);
        final int size = byteBuffer.readInt();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // skip offest to meta
        final EnumMap map = new EnumMap(enumClass);
        for (int i = 0; i < size; i++) {
            final Key valueKey = Key.read(byteBuffer);
            adapter.setValueKey(valueKey);
            if (checkForNull(adapter.lastValueAdapter, valueKey)) {
                continue;
            }
            map.put(
                    keyAdapter.deserialize(byteBuffer),
                    adapter.lastValueAdapter.deserialize(byteBuffer)
            );
        }
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return map;
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Object key) {
        if (adapter == null && settings.unknownItemStrategy == MapSettings.ItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key.getClass());
        }
        return adapter == null;
    }

    private boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter, @Nonnull Key<?> key) {
        if (adapter == null && settings.unknownItemStrategy == MapSettings.ItemStrategy.THROW_EXCEPTION) {
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

    @Nonnull
    private BinaryAdapter<Enum> getAdapterForMapKey(@Nonnull Class<Enum> enumClass) throws Exception {
        BinaryAdapter<Enum> adapter = adapterProvider.getAdapterForClass(enumClass, null);
        if (adapter == null || adapter.key().equals(EnumBinaryAdapter.KEY)) {
            adapter = new EnumNameBinaryAdapter(enumClass);
        }
        return adapter;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private BinaryAdapter<Enum> getAdapterForMapKey(@Nonnull Key<?> key, @Nonnull Class<Enum> enumClass) throws Exception {
        BinaryAdapter<?> adapter = adapterProvider.getAdapterByKey(key, null);
        if (adapter == null || adapter.key().equals(EnumBinaryAdapter.KEY)) {
            adapter = new EnumNameBinaryAdapter(enumClass);
        }
        return (BinaryAdapter<Enum>) adapter;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Class<Enum> extractEnumClass(@Nonnull EnumMap map) throws Exception {
        if (map.isEmpty()) {
            if (keyTypeField == null) {
                keyTypeField = EnumMap.class.getDeclaredField("keyType");
                keyTypeField.setAccessible(true);
            }
            return (Class<Enum>) keyTypeField.get(map);
        } else {
            return (Class<Enum>) map.keySet().iterator().next().getClass();
        }
    }

    private final class EnumNameBinaryAdapter extends AbstractBinaryAdapter<Enum> {

        @Nonnull
        private final Class<Enum> enumClass;

        EnumNameBinaryAdapter(@Nonnull Class<Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Nonnull
        @Override
        public Key<?> key() {
            return EnumBinaryAdapter.KEY;
        }

        @Override
        public int getSize(@Nonnull Enum value) throws Exception {
            return stringBinaryAdapter.getSize(value.name());
        }

        @Override
        public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Enum value) throws Exception {
            stringBinaryAdapter.serialize(byteBuffer, value.name());
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public Enum deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
            return Enum.valueOf(enumClass, stringBinaryAdapter.deserialize(byteBuffer));
        }
    }

    private static class Factory extends MapFactory<EnumMap, EnumMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected EnumMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) throws Exception {
            return new EnumMapBinaryAdapter(provider, settings);
        }
    }
}