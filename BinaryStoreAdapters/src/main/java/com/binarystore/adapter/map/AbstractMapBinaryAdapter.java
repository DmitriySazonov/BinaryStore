package com.binarystore.adapter.map;

import com.binarystore.VersionException;
import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.NullBinaryAdapter;
import com.binarystore.adapter.UnknownItemStrategy;
import com.binarystore.adapter.collection.AbstractCollectionBinaryAdapter;
import com.binarystore.adapter.collection.serialization.CollectionBinaryDeserializerV1;
import com.binarystore.adapter.collection.serialization.CollectionBinarySerializer;
import com.binarystore.adapter.map.serialization.MapBinaryDeserializer;
import com.binarystore.adapter.map.serialization.MapBinarySerializer;
import com.binarystore.buffer.ByteBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class AbstractMapBinaryAdapter<T extends Map> extends AbstractBinaryAdapter<T> {

    private final MapBinarySerializer serializer;
    private final MapBinaryDeserializer<T> deserializer;
    private final byte version = 1;

    @Nonnull
    private final MapSettings settings;
    @Nonnull
    private final BinaryAdapterProvider adapterProvider;

    protected AbstractMapBinaryAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final MapSettings settings
    ) {
        this.serializer = new MapBinarySerializer(provider, settings);
        this.deserializer = new MapBinaryDeserializer<T>(provider, settings) {

            @Override
            public T createMap(int size) throws Exception {
                return AbstractMapBinaryAdapter.this.createMap(size);
            }
        };
        this.adapterProvider = provider;
        this.settings = settings;
    }

    @Nonnull
    protected abstract T createMap(int size) throws Exception;

    protected int getSizeAdditionalMeta(@Nonnull T value) throws Exception {
        return 0;
    }

    protected void serializeAdditionalMeta(@Nonnull ByteBuffer buffer, @Nonnull T value) throws Exception {

    }

    @Override
    @SuppressWarnings("unchecked")
    public int getSize(@Nonnull T value) throws Exception {
        return serializer.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull T value) throws Exception {
        serializer.serialize(byteBuffer, value);
    }

    @Override
    @Nonnull
    public T deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return deserializer.deserialize(byteBuffer);
    }
}
