package com.binarystore.adapter.map;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.dependency.Properties;
import com.binarystore.dependency.PropertiesUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class MapBinaryAdapter extends AbstractMapBinaryAdapter<Map> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.MAP;

    @Nonnull
    private final BinaryAdapterProvider provider;
    @Nonnull
    private final Properties properties;

    protected MapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings,
            @Nonnull Properties properties
    ) {
        super(provider, settings);
        this.provider = provider;
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getSize(@Nonnull Map value) throws Exception {
        final BinaryAdapter<Map> adapter =
                (BinaryAdapter<Map>) provider.getAdapterForClass(value.getClass(), properties);
        if (adapter != null) {
            return adapter.key().getSize() + adapter.getSize(value);
        }
        return key().getSize() + super.getSize(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Map value) throws Exception {
        final BinaryAdapter<Map> adapter =
                (BinaryAdapter<Map>) provider.getAdapterForClass(value.getClass(), properties);
        if (adapter != null) {
            adapter.key().saveTo(byteBuffer);
            adapter.serialize(byteBuffer, value);
        } else {
            key().saveTo(byteBuffer);
            super.serialize(byteBuffer, value);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Map deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Key<?> key = Key.read(byteBuffer);
        if (KEY.equals(key)) {
            return super.deserialize(byteBuffer);
        } else {
            final BinaryAdapter<Map> adapter =
                    (BinaryAdapter<Map>) provider.getAdapterByKey(key, properties);
            Objects.requireNonNull(adapter);
            return adapter.deserialize(byteBuffer);
        }
    }

    @Nonnull
    @Override
    protected Map createMap(int size, @Nonnull ByteBuffer buffer) {
        return new HashMap();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static final class Factory implements AdapterFactory<Map, MapBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }


        @Nonnull
        @Override
        public MapBinaryAdapter create(@Nonnull Context context) {
            MapSettings settings = PropertiesUtils.getOrDefault(context, MapSettings.class,
                    MapSettings.defaultSettings);
            return new MapBinaryAdapter(context.getAdapterProvider(), settings, context);
        }
    }
}
