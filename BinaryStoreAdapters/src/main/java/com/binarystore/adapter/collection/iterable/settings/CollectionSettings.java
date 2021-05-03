package com.binarystore.adapter.collection.iterable.settings;

import com.binarystore.adapter.collection.UnknownItemStrategy;

import javax.annotation.Nonnull;

public class CollectionSettings  {

    public static CollectionSettings defaultSettings = new CollectionSettings(
            UnknownItemStrategy.SKIP,
            false,
            false
    );

    @Nonnull
    public final UnknownItemStrategy unknownItemStrategy;
    public final boolean isAllowNullKey;
    public final boolean isAllowNullValue;

    public CollectionSettings(
            @Nonnull UnknownItemStrategy unknownItemStrategy,
            boolean isAllowNullKey,
            boolean isAllowNullValue
    ) {
        this.unknownItemStrategy = unknownItemStrategy;

        this.isAllowNullKey = isAllowNullKey;
        this.isAllowNullValue = isAllowNullValue;
    }
}
