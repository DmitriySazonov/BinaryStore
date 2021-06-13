package com.binarystore.adapter.map.serialization;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.BinaryDeserializer;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.map.MapSettings;
import com.binarystore.adapter.map.utils.MapAdapterHelper;
import com.binarystore.adapter.map.utils.MapAdapterUtils;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class MapBinaryDeserializer<T extends Map> implements BinaryDeserializer<T> {

    public interface Delegate {
        Object deserialize(BinaryAdapter<Object> adapter, StaticByteBuffer buffer) throws Exception;
    }

    private static final Object SKIP_ITEM = new Object();
    private static final byte version = 1;

    @Nonnull
    private final BinaryAdapterProvider adapterProvider;
    @Nonnull
    private final MapSettings settings;
    @CheckForNull
    private final Delegate delegate;

    public MapBinaryDeserializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull MapSettings settings
    ) {
        this(adapterProvider, settings, null);
    }

    public MapBinaryDeserializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull MapSettings settings,
            @CheckForNull Delegate delegate
    ) {
        this.adapterProvider = adapterProvider;
        this.settings = settings;
        this.delegate = delegate;
    }

    abstract public T createMap(int size) throws Exception;

    @Override
    @SuppressWarnings("unchecked")
    @Nonnull
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final int rootOffset = byteBuffer.getOffset();
        final MapAdapterHelper adapters = new MapAdapterHelper(adapterProvider);
        MapAdapterUtils.checkVersion(byteBuffer, version);
        final int size = byteBuffer.readInt();
        final int absoluteOffsetToMeta = rootOffset + byteBuffer.readInt();
        final int[] itemOffsets = new int[size];
        byteBuffer.setOffset(absoluteOffsetToMeta);
        for (int i = 0; i < itemOffsets.length; i++) {
            itemOffsets[i] = rootOffset + byteBuffer.readInt();
        }

        final T map = createMap(size);
        final Map<Object, Object> mutableMap = (Map<Object, Object>) map;
        for (int i = 0; i < size; i++) {
            byteBuffer.setOffset(itemOffsets[i]);
            final int absoluteEndOfEntry = i + 1 < size ? itemOffsets[i + 1] : absoluteOffsetToMeta;
            Object key = deserializeKey(byteBuffer, adapters);
            Object value = deserializeValue(byteBuffer, adapters, absoluteEndOfEntry);
            if (key == SKIP_ITEM || value == SKIP_ITEM) {
                continue;
            }
            mutableMap.put(key, value);
        }
        byteBuffer.setOffset(absoluteOffsetToMeta);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return map;
    }

    private Object deserializeKey(
            @Nonnull ByteBuffer byteBuffer,
            @Nonnull MapAdapterHelper adapters
    ) throws Exception {

        final Key entryKey = Key.read(byteBuffer);
        adapters.setKeyKey(entryKey);
        if (MapAdapterUtils.checkForNull(adapters.lastKeyAdapter, entryKey, settings)) {
            return SKIP_ITEM;
        }
        return adapters.lastKeyAdapter.deserialize(byteBuffer);
    }

    private Object deserializeValue(
            @Nonnull ByteBuffer byteBuffer,
            @Nonnull MapAdapterHelper adapters,
            int endOfEntry
    ) throws Exception {
        final Key valueKey = Key.read(byteBuffer);
        adapters.setValueKey(valueKey);
        try {
            if (!MapAdapterUtils.checkForNull(adapters.lastValueAdapter, valueKey, settings)) {
                if (delegate == null) {
                    return adapters.lastValueAdapter.deserialize(byteBuffer);
                } else {
                    return delegate.deserialize(adapters.lastValueAdapter, byteBuffer
                            .getSubBuffer(byteBuffer.getOffset(), endOfEntry));
                }
            }
            return SKIP_ITEM;
        } catch (Throwable throwable) {
            if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                throw new IllegalStateException("Fail deserialize for key " + valueKey);
            }
            return SKIP_ITEM;
        }
    }
}