package com.binarystore.adapter.map.serialization;

import com.binarystore.VersionException;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.BinaryDeserializer;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.map.MapSettings;
import com.binarystore.adapter.map.utils.MapAdapterHelper;
import com.binarystore.adapter.map.utils.MapAdapterUtils;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class MapBinaryDeserializer <T extends Map> implements BinaryDeserializer<T> {

    public interface Delegate {
        Object deserialize(BinaryAdapter<Object> adapter, StaticByteBuffer buffer) throws Exception;
    }

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
        final MapAdapterHelper adapters = new MapAdapterHelper(adapterProvider);
        final byte version = byteBuffer.readByte();
        if (version != version) {
            throw new VersionException(version, version);
        }
        final int size = byteBuffer.readInt();
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES); // skip offest to meta
        final T map = createMap(size);
        final Map<Object, Object> mutableMap = (Map<Object, Object>) map;
        for (int i = 0; i < size; i++) {
            final Key entryKey = Key.read(byteBuffer);
            final Key valueKey = Key.read(byteBuffer);
            adapters.setKeyKey(entryKey);
            adapters.setValueKey(valueKey);
            if (MapAdapterUtils.checkForNull(adapters.lastKeyAdapter, entryKey, settings)) {
                continue;
            }
            if (MapAdapterUtils.checkForNull(adapters.lastValueAdapter, valueKey, settings)) {
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



}