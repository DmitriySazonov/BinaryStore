package com.binarystore.adapter;

import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public final class EnumBinaryAdapter extends AbstractBinaryAdapter<Enum> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.ENUM;

    @Nonnull
    private final BinaryAdapter<String> stringBinaryAdapter;
    @Nonnull
    private final BinaryAdapter<Class> classBinaryAdapter;

    protected EnumBinaryAdapter(
            @Nonnull BinaryAdapter<String> stringBinaryAdapter,
            @Nonnull BinaryAdapter<Class> classBinaryAdapter
    ) {
        this.stringBinaryAdapter = stringBinaryAdapter;
        this.classBinaryAdapter = classBinaryAdapter;
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    @Override
    public int getSize(@Nonnull Enum value) throws Exception {
        return classBinaryAdapter.getSize(value.getClass()) +
                stringBinaryAdapter.getSize(value.name());
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Enum value) throws Exception {
        classBinaryAdapter.serialize(byteBuffer, value.getClass());
        stringBinaryAdapter.serialize(byteBuffer, value.name());
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Enum deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        final Class<Enum> enumClass = classBinaryAdapter.deserialize(byteBuffer);
        final String enumName = stringBinaryAdapter.deserialize(byteBuffer);
        return Enum.valueOf(enumClass, enumName);
    }

    private static final class Factory implements AdapterFactory<Enum, EnumBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public EnumBinaryAdapter create(@Nonnull Context context) throws Exception {
            final BinaryAdapter<String> stringBinaryAdapter = context.provider
                    .getAdapterForClass(String.class, null);
            final BinaryAdapter<Class> classBinaryAdapter = context.provider
                    .getAdapterForClass(Class.class, null);
            return new EnumBinaryAdapter(
                    stringBinaryAdapter != null ? stringBinaryAdapter : new StringBinaryAdapter(),
                    classBinaryAdapter != null ? classBinaryAdapter : new ClassBinaryAdapter()
            );
        }
    }
}
