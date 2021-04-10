package com.binarystore.adapter;

import com.binarystore.meta.MetadataStore;

import javax.annotation.Nonnull;

public interface AdapterFactory<T> {
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

    int adapterId();

    @Nonnull
    BinaryAdapter<T> create(@Nonnull Context context);
}
