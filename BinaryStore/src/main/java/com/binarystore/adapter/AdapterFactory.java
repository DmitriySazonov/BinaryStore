package com.binarystore.adapter;

import com.binarystore.meta.MetadataStore;

import javax.annotation.Nonnull;

public interface AdapterFactory<T, A extends BinaryAdapter<T>> {
    final class Context {
        public final BinaryAdapterProvider provider;
        public final MetadataStore metadataStore;

        public Context(
                BinaryAdapterProvider provider,
                MetadataStore metadataStore
        ) {
            this.provider = provider;
            this.metadataStore = metadataStore;
        }
    }

    Key<?> adapterKey();

    @Nonnull
    A create(@Nonnull Context context) throws Exception;
}
