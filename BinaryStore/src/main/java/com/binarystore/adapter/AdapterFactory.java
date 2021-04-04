package com.binarystore.adapter;

import com.binarystore.meta.MetadataStore;

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

    BinaryAdapter<T> create(Context context);
}
