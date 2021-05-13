package com.example.binaryjson;

import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.map.MapSettings;
import com.binarystore.dependency.Property;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class SkipItemMapSettingProperty implements Property<MapSettings> {
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

    @CheckForNull
    @Override
    public MapSettings provide() {
        return new MapSettings(UnknownItemStrategy.SKIP, UnknownItemStrategy.SKIP);
    }
}
