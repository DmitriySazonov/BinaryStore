package com.binarystore.adapter.collection.map;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.DefaultAdapters;
import com.binarystore.adapter.Key;

import java.util.TreeMap;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class TreeMapBinaryAdapter extends AbstractMapBinaryAdapter<TreeMap> {

    public static final Factory factory = new Factory();
    private static final Key<?> KEY = DefaultAdapters.TREE_MAP;

    protected TreeMapBinaryAdapter(BinaryAdapterProvider provider) {
        super(provider);
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

    private static class Factory implements AdapterFactory<TreeMap, TreeMapBinaryAdapter> {

        @Override
        public Key<?> adapterKey() {
            return TreeMapBinaryAdapter.KEY;
        }

        @Nonnull
        @Override
        public TreeMapBinaryAdapter create(@Nonnull Context context) {
            return new TreeMapBinaryAdapter(context.provider);
        }
    }
}
