package com.binarystore.manager;

import com.binarystore.adapter.AdapterFactory;
import com.binarystore.adapter.AdapterFactoryRegister;
import com.binarystore.adapter.BinaryAdapter;
import com.binarystore.adapter.BinaryAdapterProvider;
import com.binarystore.adapter.Key;
import com.binarystore.dependency.Properties;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class BinaryAdapterManager implements BinaryAdapterProvider, AdapterFactoryRegister {

    private static class AdapterEntry {
        final Class<?> clazz;
        final AdapterFactory<?, ?> factory;
        private BinaryAdapter<?> adapter = null;
        private HashMap<Properties, BinaryAdapter<?>> customAdapters = new HashMap<>();

        <T> AdapterEntry(Class<T> clazz, AdapterFactory<T, ? extends BinaryAdapter<T>> factory) {
            this.clazz = clazz;
            this.factory = factory;
        }

        private BinaryAdapter<?> getAdapter(@Nonnull AdapterFactoryContext context) throws Exception {
            if (adapter == null) {
                adapter = factory.create(context);
                checkIdEqual(adapter.key(), factory.adapterKey());
            }
            return adapter;
        }

        private BinaryAdapter<?> getAdapter(
                @Nonnull AdapterFactoryContext context,
                @CheckForNull Properties properties
        ) throws Exception {
            if (properties == null) {
                return getAdapter(context);
            } else {
                return getCustomAdapter(context, properties);
            }
        }

        private BinaryAdapter<?> getCustomAdapter(
                @Nonnull AdapterFactoryContext context,
                @Nonnull Properties properties
        ) throws Exception {
            BinaryAdapter<?> adapter = customAdapters.get(properties);
            if (adapter == null) {
                adapter = factory.create(context.wrap(properties));
                customAdapters.put(properties, adapter);
            }
            return adapter;
        }
    }

    private final HashMap<Key<?>, AdapterEntry> idToEntry = new HashMap<>();
    private final HashMap<Class<?>, AdapterEntry> classToEntry = new HashMap<>();

    private final AdapterFactoryContext defaultFactoryContext;

    public BinaryAdapterManager() {
        defaultFactoryContext = new AdapterFactoryContext(this, null);
    }

    public void resolveAllAdapters() throws Exception {
        for (Map.Entry<Class<?>, AdapterEntry> entry : classToEntry.entrySet()) {
            entry.getValue().getAdapter(defaultFactoryContext);
        }
    }

    @Override
    public <T, B extends BinaryAdapter<T>> B createAdapter(
            @Nonnull AdapterFactory<T, B> factory,
            @CheckForNull Properties properties
    ) throws Exception {
        B adapter = factory.create(defaultFactoryContext);
        checkIdEqual(adapter.key(), factory.adapterKey());
        return adapter;
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <B extends BinaryAdapter<?>> B getAdapterByClass(
            @Nonnull Class<B> clazz,
            @CheckForNull Properties properties
    ) throws Exception {
        for (Map.Entry<?, AdapterEntry> entry : idToEntry.entrySet()) {
            BinaryAdapter<?> adapter = entry.getValue().getAdapter(defaultFactoryContext, properties);
            if (clazz.isInstance(adapter)) {
                return (B) adapter;
            }
        }
        return null;
    }

    @Override
    public <T> void register(
            @Nonnull Class<T> clazz,
            @Nonnull AdapterFactory<T, ? extends BinaryAdapter<T>> factory
    ) {
        final AdapterEntry entry = new AdapterEntry(clazz, factory);
        idToEntry.put(factory.adapterKey(), entry);
        classToEntry.put(clazz, entry);
    }

    @Override
    @CheckForNull
    @SuppressWarnings("unchecked")
    public <T> BinaryAdapter<T> getAdapterForClass(
            @Nonnull Class<T> clazz,
            @CheckForNull Properties properties
    ) throws Exception {
        AdapterEntry entry = classToEntry.get(clazz);
        BinaryAdapter<T> adapter = entry != null ?
                (BinaryAdapter<T>) entry.getAdapter(defaultFactoryContext, properties) : null;
        if (adapter == null && clazz.isEnum() && clazz != Enum.class) {
            adapter = (BinaryAdapter<T>) getAdapterForClass(Enum.class, properties);
        }
        return adapter;
    }

    @Override
    @CheckForNull
    public BinaryAdapter<?> getAdapterByKey(
            @Nonnull Key<?> key,
            @CheckForNull Properties properties
    ) throws Exception {
        AdapterEntry entry = idToEntry.get(key);
        return entry != null ? entry.getAdapter(defaultFactoryContext, properties) : null;
    }

    private static void checkIdEqual(Key<?> adapterId, Key<?> factoryId) throws IllegalStateException {
        if (!adapterId.equals(factoryId)) {
            throw new IllegalStateException("ID(" + adapterId + ") of adapter doesn't " +
                    "equal ID(" + factoryId + ") of factory");
        }
    }
}
