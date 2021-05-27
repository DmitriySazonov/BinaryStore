package com.binarystore.adapter.map.utils;

import com.binarystore.VersionException;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.map.MapSettings;
import com.binarystore.buffer.ByteBuffer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class MapAdapterUtils {

    public static void checkVersion(@Nonnull ByteBuffer byteBuffer, final byte currentVersion) throws Exception {
        final byte version = byteBuffer.readByte();
        if (currentVersion != version) {
            throw new VersionException(currentVersion, version);
        }
    }

    public static boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter,
                                        @Nonnull Object key,
                                        @Nonnull MapSettings settings) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key.getClass());
        }
        return adapter == null;
    }

    public static boolean checkForNull(@CheckForNull BinaryAdapter<?> adapter,
                                       @Nonnull Key<?> key,
                                       @Nonnull MapSettings settings) {
        if (adapter == null && settings.unknownItemStrategy == UnknownItemStrategy.THROW_EXCEPTION) {
            throw new IllegalArgumentException("Couldn't find adapter for class " + key);
        }
        return adapter == null;
    }
}
