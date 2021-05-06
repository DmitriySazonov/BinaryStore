package com.binarystore.adapter;


import com.binarystore.buffer.ByteBuffer;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public final class ClassBinaryAdapter extends AbstractBinaryAdapter<Class> {

    public static final Factory factory = new Factory();
    private static final Key.Byte KEY = DefaultAdapters.CLASS;

    private final BinaryAdapter<String> stringBinaryAdapter;

    public ClassBinaryAdapter() {
        this(new StringBinaryAdapter());
    }

    protected ClassBinaryAdapter(@Nonnull final BinaryAdapter<String> stringBinaryAdapter) {
        this.stringBinaryAdapter = stringBinaryAdapter;
    }

    @Nonnull
    @Override
    public Key.Byte key() {
        return KEY;
    }

    @Override
    public int getSize(@Nonnull Class value) throws Exception {
        return stringBinaryAdapter.getSize(value.getName());
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull Class value) throws Exception {
        stringBinaryAdapter.serialize(byteBuffer, value.getName());
    }

    @Nonnull
    @Override
    public Class deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return Class.forName(stringBinaryAdapter.deserialize(byteBuffer));
    }

    public static final class Factory implements AdapterFactory<Class, ClassBinaryAdapter> {

        @Override
        public Key.Byte adapterKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public ClassBinaryAdapter create(@Nonnull Context context) throws Exception {
            BinaryAdapter<String> adapter =
                    context.getAdapterProvider().getAdapterForClass(String.class, null);
            return adapter != null ? new ClassBinaryAdapter(adapter) : new ClassBinaryAdapter();
        }
    }
}
