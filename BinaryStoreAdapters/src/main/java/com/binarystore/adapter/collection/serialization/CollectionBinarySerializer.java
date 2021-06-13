package com.binarystore.adapter.collection.serialization;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.BinarySerializer;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.utils.CollectionAdapterHelper;
import com.binarystore.adapter.collection.utils.CollectionAdapterUtils;
import com.binarystore.buffer.ByteBuffer;

import java.util.Iterator;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public final class CollectionBinarySerializer<Collection extends java.util.Collection>
        implements BinarySerializer<Collection> {

    public interface Delegate<Collection extends java.util.Collection> {
        int NOT_OVERRIDE = -1;

        boolean overrideItemSerialize(ByteBuffer buffer, Object item);

        int overrideItemGetSize(Object item);

        @CheckForNull
        Iterator<Object> overrideIterator(Collection collection);
    }

    private static final byte version = 1;

    @Nonnull
    private final BinaryAdapterProvider adapterProvider;
    @Nonnull
    private final CollectionSettings settings;
    @CheckForNull
    private final Delegate<Collection> delegate;

    public CollectionBinarySerializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull CollectionSettings settings
    ) {
        this(adapterProvider, settings, null);
    }

    public CollectionBinarySerializer(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull CollectionSettings settings,
            @CheckForNull Delegate<Collection> delegate
    ) {
        this.adapterProvider = adapterProvider;
        this.settings = settings;
        this.delegate = delegate;
    }

    @Override
    public final int getSize(@Nonnull Collection value) throws Exception {
        final CollectionAdapterHelper adapters = new CollectionAdapterHelper(adapterProvider);
        int accumulator = 0;
        int elementCount = 0;

        Iterator iterator = delegate != null ?
                delegate.overrideIterator(value) : null;
        if (iterator == null) {
            iterator = value.iterator();
        }
        Object element;
        while (iterator.hasNext()) {
            int itemSize = 0;
            element = iterator.next();
            if (delegate != null) {
                itemSize = delegate.overrideItemGetSize(element);
                if (itemSize == Delegate.NOT_OVERRIDE) {
                    itemSize = 0;
                }
            }
            if (itemSize == 0) {
                adapters.setValueClass(element);
                if (CollectionAdapterUtils.checkForNull(adapters.lastValueAdapter, element, settings)) {
                    continue;
                }
                try {
                    itemSize += adapters.lastValueAdapter.key().getSize();
                    itemSize += adapters.lastValueAdapter.getSize(element);
                } catch (Throwable throwable) {
                    if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                        throw new IllegalStateException("Fail getSize for class " + element.getClass());
                    }
                    continue;
                }
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
    public final void serialize(
            @Nonnull ByteBuffer buffer,
            @Nonnull Collection value
    ) throws Exception {
        int index = 0;
        final int rootOffset = buffer.getOffset();
        final CollectionAdapterHelper adapters = new CollectionAdapterHelper(adapterProvider);
        final int[] offsets = new int[value.size()];
        buffer.write(version);
        final int startOffset = buffer.getOffset();
        buffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for size
        buffer.moveOffset(ByteBuffer.INTEGER_BYTES); // space for offset to meta

        Iterator iterator = delegate != null ?
                delegate.overrideIterator(value) : null;
        if (iterator == null) {
            iterator = value.iterator();
        }
        Object element;

        while (iterator.hasNext()) {
            element = iterator.next();
            if (delegate != null && delegate.overrideItemSerialize(buffer, element)) {
                continue;
            }
            adapters.setValueClass(element);
            if (CollectionAdapterUtils.checkForNull(adapters.lastValueAdapter, element, settings)) {
                continue;
            }
            final int offset = buffer.getOffset();
            try {
                adapters.lastValueAdapter.key().saveTo(buffer);
                adapters.lastValueAdapter.serialize(buffer, element);
                offsets[index++] = offset - rootOffset;
            } catch (Throwable throwable) {
                if (settings.exceptionItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
                    throw new IllegalStateException("Fail serialization for class " + element.getClass(), throwable);
                }
                buffer.setOffset(offset);
            }
        }
        final int endDataOffset = buffer.getOffset();
        buffer.setOffset(startOffset);
        buffer.write(index); // write actual size of collection
        buffer.write(endDataOffset - rootOffset); // write relative offset to start of meta
        buffer.setOffset(endDataOffset); // move to the end to write meta
        for (int i = 0; i < index; i++) {
            buffer.write(offsets[i]);
        }
    }
}
