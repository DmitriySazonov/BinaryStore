package com.binarystore.adapter.collection.utils;

import com.binarystore.VersionException;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class CollectionAdapterUtils {

    public static void checkVersion(@Nonnull ByteBuffer byteBuffer, final byte currentVersion) throws Exception {
        final byte version = byteBuffer.readByte();
        if (currentVersion != version) {
            throw new VersionException(currentVersion, version);
        }
    }

    public static void checkSubCollectionBounds(final int startIndex, final int endIndex, final int collectionSize) throws RuntimeException {
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + startIndex);
        } else if (endIndex > collectionSize) {
            throw new IndexOutOfBoundsException("toIndex = " + endIndex);
        } else if (startIndex > endIndex) {
            throw new IllegalArgumentException("fromIndex(" + startIndex + ") > toIndex(" + endIndex + ")");
        }
    }

    public static boolean checkForNull(
            @CheckForNull BinaryAdapter<?> adapter,
            @Nonnull Object key,
            @Nonnull CollectionSettings settings
    ) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key.getClass());
        }
        return adapter == null;
    }

    public static boolean checkForNull(
            @CheckForNull BinaryAdapter<?> adapter,
            @Nonnull Key<?> key,
            @Nonnull CollectionSettings settings
    ) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key);
        }
        return adapter == null;
    }

    @SuppressWarnings("unchecked")
    public static BinaryAdapter<Object> getAdapterForKey(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull Key<?> key
    ) throws Exception {
        final BinaryAdapter<?> adapter;
        if (key.equals(NullBinaryAdapter.instance.key())) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterByKey(key, null);
        }
        return (BinaryAdapter<Object>) adapter;
    }

    @SuppressWarnings("unchecked")
    public static BinaryAdapter<Object> getAdapterForClass(
            @Nonnull BinaryAdapterProvider adapterProvider,
            @Nonnull Class<?> clazz
    ) throws Exception {
        final BinaryAdapter<?> adapter;
        if (clazz == NullBinaryAdapter.NULL_CLASS) {
            adapter = NullBinaryAdapter.instance;
        } else {
            adapter = adapterProvider.getAdapterForClass(clazz, null);
        }
        return (BinaryAdapter<Object>) adapter;
    }
}
