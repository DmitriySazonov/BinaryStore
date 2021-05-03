package com.binarystore.adapter;

import com.binarystore.dependency.Dependencies;
import com.binarystore.meta.MetadataStore;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface AdapterFactory<T, A extends BinaryAdapter<T>> {
    final class Context implements Dependencies {
        public final BinaryAdapterProvider provider;
        private final Dependencies optionalDependencies;

        public Context(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull Dependencies dependencies
        ) {
            this.provider = provider;
            this.optionalDependencies = dependencies;
        }

        @CheckForNull
        @Override
        public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
            return optionalDependencies.get(tClass, name);
        }
    }

    Key<?> adapterKey();

    @Nonnull
    A create(@Nonnull Context context) throws Exception;
}
