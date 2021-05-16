package com.binarystore.adapter.collection.serialization;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.BinaryDeserializer;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.utils.AdapterHelper;
import com.binarystore.adapter.collection.utils.CollectionAdapterUtils;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class CollectionBinaryDeserializerV1<T extends Collection> implements BinaryDeserializer<T> {

    public interface Delegate {
        Object deserialize(BinaryAdapter<Object> adapter, StaticByteBuffer buffer) throws Exception;
    }

    private static final Object SKIP_ITEM = new Object();
    private static final byte version = 1;

    @Nonnull
    private final BinaryAdapterProvider adapterProvider;
    @Nonnull
    private final CollectionSettings settings;
    @CheckForNull
    private final Delegate delegate;

    public CollectionBinaryDeserializerV1(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull CollectionSettings settings
    ) {
        this(adapterProvider, settings, null);
    }

    public CollectionBinaryDeserializerV1(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull CollectionSettings settings,
            @CheckForNull Delegate delegate
    ) {
        this.adapterProvider = adapterProvider;
        this.settings = settings;
        this.delegate = delegate;
    }

    abstract public T createCollection(int size);

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public final T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final int rootOffset = byteBuffer.getOffset();
        final AdapterHelper adapters = new AdapterHelper(adapterProvider);
        CollectionAdapterUtils.checkVersion(byteBuffer, version);
        final int size = byteBuffer.readInt();
        final int absoluteOffsetToMeta = rootOffset + byteBuffer.readInt();
        final int[] itemOffsets = new int[size];
        byteBuffer.setOffset(absoluteOffsetToMeta);
        for (int i = 0; i < itemOffsets.length; i++) {
            itemOffsets[i] = rootOffset + byteBuffer.readInt();
        }
        final T collection = createCollection(size);
        for (int i = 0; i < size; i++) {
            byteBuffer.setOffset(itemOffsets[i]);
            final int relativeEndOfEntry = i + 1 < size ? itemOffsets[i + 1] : absoluteOffsetToMeta;
            final Object element = deserializeElement(byteBuffer, adapters, relativeEndOfEntry);
            if (element != SKIP_ITEM) {
                collection.add(element);
            }
        }
        byteBuffer.setOffset(absoluteOffsetToMeta);
        byteBuffer.moveOffset(ByteBuffer.INTEGER_BYTES * size);
        return collection;
    }

    private Object deserializeElement(
            @Nonnull ByteBuffer byteBuffer,
            @Nonnull AdapterHelper adapters,
            int endOfEntry
    ) throws Exception {
        final Key valueKey = Key.read(byteBuffer);
        adapters.setValueKey(valueKey);
        try {
            if (!CollectionAdapterUtils.checkForNull(adapters.lastValueAdapter, valueKey, settings)) {
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
