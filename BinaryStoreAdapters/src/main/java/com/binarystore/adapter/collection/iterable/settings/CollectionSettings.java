package com.binarystore.adapter.collection.iterable.settings;

import com.binarystore.adapter.collection.UnknownItemStrategy;
import com.binarystore.adapter.collection.map.MapSettings;
import com.binarystore.dependency.Property;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class CollectionSettings {

    public static CollectionSettings defaultSettings = new CollectionSettings(
            UnknownItemStrategy.SKIP, UnknownItemStrategy.SKIP
    );

    @Nonnull
    public final UnknownItemStrategy unknownItemStrategy;
    @Nonnull
    public final UnknownItemStrategy exceptionItemStrategy;

    public CollectionSettings(
            @Nonnull UnknownItemStrategy unknownItemStrategy,
            @Nonnull UnknownItemStrategy exceptionItemStrategy
    ) {
        this.unknownItemStrategy = unknownItemStrategy;
        this.exceptionItemStrategy = exceptionItemStrategy;
    }

    public abstract static class AbstractProperty implements Property<CollectionSettings> {

        @CheckForNull
        @Override
        public String name() {
            return null;
        }

        @Nonnull
        @Override
        public Class<CollectionSettings> typeClass() {
            return CollectionSettings.class;
        }
    }

    public final static class SkipItemSettingProperty extends AbstractProperty {

        private static final CollectionSettings settings = new CollectionSettings(UnknownItemStrategy.SKIP,
                UnknownItemStrategy.SKIP);

        @Override
        public CollectionSettings provide() {
            return settings;
        }
    }

    public final static class CrashExceptionSettingProperty extends AbstractProperty {

        private static final CollectionSettings settings = new CollectionSettings(UnknownItemStrategy.THROW_EXCEPTION,
                UnknownItemStrategy.THROW_EXCEPTION);

        @Override
        public CollectionSettings provide() {
            return settings;
        }
    }
}
