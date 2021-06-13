package com.binarystore.adapter.map;

import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;
import com.binarystore.buffer.ByteBuffer;

import java.util.TreeMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class TreeMapBinaryAdapter extends AbstractMapBinaryAdapter<TreeMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.TREE_MAP;

    protected TreeMapBinaryAdapter(
            @Nonnull BinaryAdapterProvider provider,
            @Nonnull MapSettings settings
    ) {
        super(provider, settings);
    }

    @Nonnull
    @Override
    @SuppressWarnings("SortedCollectionWithNonComparableKeys")
    protected TreeMap<?, ?> createMap(int size) {
        return new TreeMap<>();
    }

    @Nonnull
    @Override
    public Key<?> key() {
        return KEY;
    }

    private static class Factory extends MapFactory<TreeMap, TreeMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return TreeMapBinaryAdapter.KEY;
        }

        @Override
        protected TreeMapBinaryAdapter create(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull MapSettings settings
        ) {
            return new TreeMapBinaryAdapter(provider, settings);
        }
    }
}
