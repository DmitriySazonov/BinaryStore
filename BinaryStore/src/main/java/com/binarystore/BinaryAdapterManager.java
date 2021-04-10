package com.binarystore;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.AdapterFactoryRegister;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.meta.MetadataStore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class BinaryAdapterManager implements BinaryAdapterProvider, AdapterFactoryRegister {

    private static class FactoryEntry {
        final Class<?> clazz;
        final AdapterFactory<?> factory;

        <T> FactoryEntry(Class<T> clazz, AdapterFactory<T> factory) {
            this.clazz = clazz;
            this.factory = factory;
        }
    }

    private final HashMap<Integer, FactoryEntry> idToFactory = new HashMap<>();
    private final HashMap<Class<?>, AdapterFactory<?>> classToFactory = new HashMap<>();

    private final HashMap<Integer, BinaryAdapter<?>> idToAdapter = new HashMap<>();
    private final HashMap<Class<?>, BinaryAdapter<?>> classToAdapter = new HashMap<>();

    private final AdapterFactory.Context factoryContext;

    public BinaryAdapterManager(MetadataStore metadataStore) {
        factoryContext = new AdapterFactory.Context(this, metadataStore);
    }

    public void resolveAllAdapters() {
        for (Map.Entry<Class<?>, AdapterFactory<?>> entry : classToFactory.entrySet()) {
            getAdapter(entry.getKey());
        }
    }

    @Override
    public <T> void register(@Nonnull Class<T> clazz, @Nonnull AdapterFactory<T> factory) {
        idToFactory.put(factory.adapterId(), new FactoryEntry(clazz, factory));
        classToFactory.put(clazz, factory);
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <T> BinaryAdapter<T> getAdapter(@Nonnull Class<T> clazz) {
        BinaryAdapter<?> adapter = classToAdapter.get(clazz);
        if (adapter == null) {
            AdapterFactory<?> factory = classToFactory.get(clazz);
            if (factory != null) {
                adapter = factory.create(factoryContext);
            } else {
                return null;
            }
            checkIdEqual(adapter.id(), factory.adapterId());
            classToAdapter.put(clazz, adapter);
            idToAdapter.put(adapter.id(), adapter);
        }

        return (BinaryAdapter<T>) adapter;
    }

    @Override
    @CheckForNull
    public BinaryAdapter<?> getAdapter(int id) {
        BinaryAdapter<?> adapter = idToAdapter.get(id);
        if (adapter == null) {
            FactoryEntry entry = idToFactory.get(id);
            AdapterFactory<?> factory = entry.factory;
            if (factory != null) {
                adapter = factory.create(factoryContext);
            } else {
                return null;
            }
            checkIdEqual(adapter.id(), factory.adapterId());
            classToAdapter.put(entry.clazz, adapter);
            idToAdapter.put(adapter.id(), adapter);
        }

        return adapter;
    }

    private void checkIdEqual(int adapterId, int factoryId) {
        if (adapterId != factoryId) {
            throw new IllegalStateException("ID(" + adapterId + ") of adapter doesn't " +
                    "equal ID(" + factoryId + ") of factory");
        }
    }
}
