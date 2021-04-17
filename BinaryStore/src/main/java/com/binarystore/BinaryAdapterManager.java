package com.binarystore;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.AdapterFactoryRegister;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.meta.MetadataStore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class BinaryAdapterManager implements BinaryAdapterProvider, AdapterFactoryRegister {

    private static class AdapterEntry {
        final Class<?> clazz;
        final AdapterFactory<?> factory;
        private BinaryAdapter<?> adapter = null;

        <T> AdapterEntry(Class<T> clazz, AdapterFactory<T> factory) {
            this.clazz = clazz;
            this.factory = factory;
        }

        private BinaryAdapter<?> getAdapter(AdapterFactory.Context context) {
            if (adapter == null) {
                adapter = factory.create(context);
                checkIdEqual(adapter.id(), factory.adapterKey());
            }
            return adapter;
        }
    }

    private final HashMap<Key<?>, AdapterEntry> idToEntry = new HashMap<>();
    private final HashMap<Class<?>, AdapterEntry> classToEntry = new HashMap<>();

    private final AdapterFactory.Context factoryContext;

    public BinaryAdapterManager(MetadataStore metadataStore) {
        factoryContext = new AdapterFactory.Context(this, metadataStore);
    }

    public void resolveAllAdapters() {
        for (Map.Entry<Class<?>, AdapterEntry> entry : classToEntry.entrySet()) {
            entry.getValue().getAdapter(factoryContext);
        }
    }

    @Override
    public <T> void register(@Nonnull Class<T> clazz, @Nonnull AdapterFactory<T> factory) {
        final AdapterEntry entry = new AdapterEntry(clazz, factory);
        idToEntry.put(factory.adapterKey(), entry);
        classToEntry.put(clazz, entry);
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <T> BinaryAdapter<T> getAdapter(@Nonnull Class<T> clazz) {
        AdapterEntry entry = classToEntry.get(clazz);
        return entry != null ? (BinaryAdapter<T>) entry.getAdapter(factoryContext) : null;
    }

    @Override
    @CheckForNull
    public BinaryAdapter<?> getAdapter(Key<?> id) {
        AdapterEntry entry = idToEntry.get(id);
        return entry != null ? entry.getAdapter(factoryContext) : null;
    }

    private static void checkIdEqual(Key<?> adapterId, Key<?> factoryId) {
        if (!adapterId.equals(factoryId)) {
            throw new IllegalStateException("ID(" + adapterId + ") of adapter doesn't " +
                    "equal ID(" + factoryId + ") of factory");
        }
    }
}
