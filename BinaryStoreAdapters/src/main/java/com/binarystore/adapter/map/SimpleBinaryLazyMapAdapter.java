package com.binarystore.adapter.map;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.LazyBinaryEntry;
import com.binarystore.adapter.map.serialization.MapBinaryDeserializer;
import com.binarystore.adapter.map.serialization.MapBinarySerializer;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;
import com.binarystore.map.SimpleBinaryLazyMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class SimpleBinaryLazyMapAdapter extends AbstractBinaryAdapter<SimpleBinaryLazyMap>
        implements MapBinaryDeserializer.Delegate {

    public static final SimpleBinaryLazyMapAdapter.Factory factory = new SimpleBinaryLazyMapAdapter.Factory();
    public static final Key.Byte KEY = DefaultAdapters.SIMPLE_LAZY_LIST;
    private final MapBinarySerializer serializer;
    private final MapBinaryDeserializer<Map> deserializer;

    protected SimpleBinaryLazyMapAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final MapSettings settings
    ) {
        this.serializer = new MapBinarySerializer(provider, settings, true);
        this.deserializer = new MapBinaryDeserializer<Map>(provider, settings, this) {

            @Override
            public Map createMap(int size) {
                return new HashMap(size);
            }
        };
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    @Override
    public int getSize(@Nonnull SimpleBinaryLazyMap value) throws Exception {
        return serializer.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull SimpleBinaryLazyMap value) throws Exception {
        serializer.serialize(byteBuffer, value);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public SimpleBinaryLazyMap deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return new SimpleBinaryLazyMap(deserializer.deserialize(byteBuffer));
    }

    @Override
    public Object deserialize(BinaryAdapter<Object> adapter, StaticByteBuffer buffer) {
        return new LazyBinaryEntry<>(buffer, adapter);
    }

    private static final class Factory extends MapFactory<SimpleBinaryLazyMap, SimpleBinaryLazyMapAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected SimpleBinaryLazyMapAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings) {
            return new SimpleBinaryLazyMapAdapter(provider, settings);
        }
    }
}
