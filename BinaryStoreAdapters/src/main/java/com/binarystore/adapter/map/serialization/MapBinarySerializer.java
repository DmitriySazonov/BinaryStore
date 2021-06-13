package com.binarystore.adapter.map.serialization;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.BinarySerializer;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.map.MapSettings;
import com.binarystore.adapter.map.utils.MapAdapterHelper;
import com.binarystore.adapter.map.utils.MapAdapterUtils;
import com.binarystore.buffer.ByteBuffer;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class MapBinarySerializer implements BinarySerializer<Map> {

    private static final byte version = 1;
    private final boolean allowUseValueAsAdapter;

    @Nonnull
    private final BinaryAdapterProvider adapterProvider;
    @Nonnull
    private final MapSettings settings;

    public MapBinarySerializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull MapSettings settings
    ) {
        this(adapterProvider, settings, false);
    }

    public MapBinarySerializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull MapSettings settings,
            final boolean allowUseValueAsAdapter
    ) {
        this.adapterProvider = adapterProvider;
        this.settings = settings;
        this.allowUseValueAsAdapter = allowUseValueAsAdapter;
    }

    /**
     * Scheme
     * version - 1 byte
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
    public int getSize(@Nonnull Map value) throws Exception {
        final MapAdapterHelper adapters = new MapAdapterHelper(adapterProvider, allowUseValueAsAdapter);
        final Set<Map.Entry> entries = value.entrySet();
        int actualSize = 0;
        int accumulator = 0;
        for (Map.Entry entry : entries) {
            final Object entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapters.setKeyClass(entryKey);
            adapters.setValueClass(entryValue);
            if (MapAdapterUtils.checkForNull(adapters.lastKeyAdapter, entryKey, settings)) {
                continue;
            }
            if (MapAdapterUtils.checkForNull(adapters.lastValueAdapter, entryValue, settings)) {
                continue;
            }
            int itemSize = 0;
            try {
                itemSize += adapters.lastKeyAdapter.key().getSize();
                itemSize += adapters.lastValueAdapter.key().getSize();
                itemSize += adapters.lastKeyAdapter.getSize(entryKey);
                itemSize += adapters.lastValueAdapter.getSize(entryValue);
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail getSize for key " + entryKey);
                }
                continue;
            }
            actualSize++;
            accumulator += itemSize;
        }
        return (ByteBuffer.BYTE_BYTES +  // version
                ByteBuffer.INTEGER_BYTES +  // map size
                ByteBuffer.INTEGER_BYTES +  // offset to meta
                accumulator +  // data size
                ByteBuffer.INTEGER_BYTES * actualSize // entries offsets
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(@Nonnull ByteBuffer buffer, @Nonnull Map value) throws Exception {
        int index = 0;
        final int rootOffset = buffer.getOffset();
        final MapAdapterHelper adapters = new MapAdapterHelper(adapterProvider, allowUseValueAsAdapter);
        final int[] offsets = new int[value.size()];
        buffer.write(version);
        final int startOffset = buffer.getOffset();
        buffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        buffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta
        final Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            final int offset = buffer.getOffset();
            final Object entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            adapters.setKeyClass(entryKey);
            adapters.setValueClass(entryValue);
            if (MapAdapterUtils.checkForNull(adapters.lastKeyAdapter, entryKey, settings)) {
                continue;
            }
            if (MapAdapterUtils.checkForNull(adapters.lastValueAdapter, entryValue, settings)) {
                continue;
            }

            try {
                adapters.lastKeyAdapter.key().saveTo(buffer);
                adapters.lastKeyAdapter.serialize(buffer, entryKey);
                adapters.lastValueAdapter.key().saveTo(buffer);
                adapters.lastValueAdapter.serialize(buffer, entryValue);
                offsets[index++] = offset - rootOffset;
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail serialization for key " + entryKey);
                }
                buffer.setOffset(offset);
            }
        }
        final int endDataOffset = buffer.getOffset();
        buffer.setOffset(startOffset);
        buffer.write(index); // write actual size of map
        buffer.write(endDataOffset - rootOffset); // write offset to start of meta
        buffer.setOffset(endDataOffset); // move to the end to write meta
        for (int i = 0; i < index; i++) {
            buffer.write(offsets[i]);
        }
    }
}
