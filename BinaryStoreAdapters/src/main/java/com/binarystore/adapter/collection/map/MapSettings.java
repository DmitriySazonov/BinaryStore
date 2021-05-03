package com.binarystore.adapter.collection.map;

import javax.annotation.Nonnull;

public final class MapSettings {

    public static MapSettings defaultSettings = new MapSettings(
            UnknownItemStrategy.SKIP,
            false,
            false
    );

    public enum UnknownItemStrategy {
        SKIP, THROW_EXCEPTION
    }

    @Nonnull
    public final UnknownItemStrategy unknownItemStrategy;
    public final boolean isAllowNullKey;
    public final boolean isAllowNullValue;

    public MapSettings(
            @Nonnull UnknownItemStrategy unknownItemStrategy,
            boolean isAllowNullKey,
            boolean isAllowNullValue
    ) {
        this.unknownItemStrategy = unknownItemStrategy;

        this.isAllowNullKey = isAllowNullKey;
        this.isAllowNullValue = isAllowNullValue;
    }
}
