package com.binarystore.adapter.map;

import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.ClassBinaryAdapter;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.lang.reflect.Field;
import java.util.EnumMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class EnumMapBinaryAdapter extends AbstractMapBinaryAdapter<EnumMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.ENUM_MAP;
    private static Field keyTypeField = null;

    @Nonnull
    private final BinaryAdapter<Class> classBinaryAdapter;

    protected EnumMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) throws Exception {
        super(provider, settings);
        final BinaryAdapter<Class> adapter =
                provider.getAdapterForClass(Class.class, null);
        this.classBinaryAdapter = adapter != null ? adapter : new ClassBinaryAdapter();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    protected EnumMap createMap(int size, @Nonnull ByteBuffer buffer) throws Exception {
        Class enumClass = classBinaryAdapter.deserialize(buffer);
        return new EnumMap(enumClass);
    }

    @Override
    protected int getSizeAdditionalMeta(@Nonnull EnumMap value) throws Exception {
        return super.getSizeAdditionalMeta(value) +
                classBinaryAdapter.getSize(extractKeyClass(value));
    }

    @Override
    protected void serializeAdditionalMeta(@Nonnull ByteBuffer buffer, @Nonnull EnumMap value) throws Exception {
        super.serializeAdditionalMeta(buffer, value);
        classBinaryAdapter.serialize(buffer, extractKeyClass(value));
    }

    private Class extractKeyClass(@Nonnull EnumMap value) throws NoSuchFieldException, IllegalAccessException {
        if (value.isEmpty()) {
            if (keyTypeField == null) {
                keyTypeField = EnumMap.class.getDeclaredField("keyType");
            }
            return (Class) keyTypeField.get(value);
        } else {
            return value.keySet().iterator().next().getClass();
        }
    }

    private static class Factory extends MapFactory<EnumMap, EnumMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }

        @Override
        protected EnumMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) throws Exception {
            return new EnumMapBinaryAdapter(provider, settings);
        }
    }
}
