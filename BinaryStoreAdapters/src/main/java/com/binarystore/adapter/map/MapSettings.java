package com.binarystore.adapter.map;

import com.binarystore.dependency.Property;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public final class MapSettings {

    public static MapSettings defaultSettings = new MapSettings(
            ItemStrategy.SKIP, ItemStrategy.SKIP
    );

    public enum ItemStrategy {
        SKIP, THROW_EXCEPTION
    }

    @Nonnull
    public final ItemStrategy unknownItemStrategy;
    @Nonnull
    public final ItemStrategy exceptionItemStrategy;

    public MapSettings(
            @Nonnull ItemStrategy unknownItemStrategy,
            @Nonnull ItemStrategy exceptionItemStrategy
    ) {
        this.unknownItemStrategy = unknownItemStrategy;
        this.exceptionItemStrategy = exceptionItemStrategy;
    }

    public abstract static class AbstractProperty implements Property<MapSettings> {

        @CheckForNull
        @Override
        public String name() {
            return null;
        }

        @Nonnull
        @Override
        public Class<MapSettings> typeClass() {
            return MapSettings.class;
        }
    }

    public final static class SkipItemSettingProperty extends AbstractProperty {

        private static final MapSettings settings = new MapSettings(ItemStrategy.SKIP,
                ItemStrategy.SKIP);

        @Override
        public MapSettings provide() {
            return settings;
        }
    }

    public final static class ThrowExceptionSettingProperty extends AbstractProperty {

        private static final MapSettings settings = new MapSettings(ItemStrategy.THROW_EXCEPTION,
                ItemStrategy.THROW_EXCEPTION);

        @Override
        public MapSettings provide() {
            return settings;
        }
    }
}
