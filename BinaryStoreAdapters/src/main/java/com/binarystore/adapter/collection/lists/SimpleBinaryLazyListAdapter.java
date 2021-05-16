package com.binarystore.adapter.collection.lists;

import com.binarystore.adapter.AbstractBinaryAdapter;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.adapter.LazyBinaryEntry;
import com.binarystore.adapter.collection.CollectionFactory;
import com.binarystore.adapter.collection.CollectionSettings;
import com.binarystore.adapter.collection.serialization.CollectionBinaryDeserializerV1;
import com.binarystore.adapter.collection.serialization.CollectionBinarySerializer;
import com.binarystore.buffer.ByteBuffer;
import com.binarystore.buffer.StaticByteBuffer;
import com.binarystore.collections.SimpleBinaryLazyList;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public final class SimpleBinaryLazyListAdapter extends AbstractBinaryAdapter<SimpleBinaryLazyList>
        implements CollectionBinaryDeserializerV1.Delegate {

    public static final Factory factory = new Factory();
    public static final Key.Byte KEY = DefaultAdapters.SIMPLE_LAZY_LIST;
    private final CollectionBinarySerializer serializer;
    private final CollectionBinaryDeserializerV1<Collection> deserializer;

    protected SimpleBinaryLazyListAdapter(
            @Nonnull final BinaryAdapterProvider provider,
            @Nonnull final CollectionSettings settings
    ) {
        this.serializer = new CollectionBinarySerializer(provider, settings, true);
        this.deserializer = new CollectionBinaryDeserializerV1<Collection>(provider, settings, this) {

            @Override
            public Collection createCollection(int size) {
                return new ArrayList(size);
            }
        };
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    @Override
    public int getSize(@Nonnull SimpleBinaryLazyList value) throws Exception {
        return serializer.getSize(value);
    }

    @Override
    public void serialize(@Nonnull ByteBuffer byteBuffer, @Nonnull SimpleBinaryLazyList value) throws Exception {
        serializer.serialize(byteBuffer, value);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public SimpleBinaryLazyList deserialize(@Nonnull ByteBuffer byteBuffer) throws Exception {
        return new SimpleBinaryLazyList(deserializer.deserialize(byteBuffer));
    }

    @Override
    public Object deserialize(BinaryAdapter<Object> adapter, StaticByteBuffer buffer) {
        return new LazyBinaryEntry<>(buffer, adapter);
    }

    private static final class Factory extends CollectionFactory<SimpleBinaryLazyList, SimpleBinaryLazyListAdapter> {

        @Override
        protected SimpleBinaryLazyListAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull CollectionSettings settings
        ) {
            return new SimpleBinaryLazyListAdapter(provider, settings);
        }

        @Override
        public Key<?> adapterKey() {
            return KEY;
        }
    }
}
