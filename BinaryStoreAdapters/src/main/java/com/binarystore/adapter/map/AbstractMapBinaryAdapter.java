package com.binarystore.adapter.map;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.map.serialization.MapBinaryDeserializer;
import com.binarystore.adapter.map.serialization.MapBinarySerializer;
import com.binarystore.buffer.ByteBuffer;

import java.util.Map;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class AbstractMapBinaryAdapter<T extends Map> extends AbstractBinaryAdapter<T> {

    @Nonnull
    private final MapBinarySerializer serializer;
    @Nonnull
    private final MapBinaryDeserializer<T> deserializer;

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
    }

    @Nonnull
    protected abstract T createMap(int size) throws Exception;

    @Override
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
