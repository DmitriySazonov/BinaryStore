package com.binarystore.adapter;

import com.binarystore.dependency.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface AdapterFactory<T, A extends BinaryAdapter<T>> {
    final class Context implements Properties {
        public final BinaryAdapterProvider provider;
        private final Properties optionalProperties;

        public Context(
                @Nonnull BinaryAdapterProvider provider,
                @Nonnull Properties properties
        ) {
            this.provider = provider;
            this.optionalProperties = properties;
        }

        @CheckForNull
        @Override
        public <T> T get(@Nonnull Class<T> tClass, @CheckForNull String name) {
            return optionalProperties.get(tClass, name);
        }
    }

    Key<?> adapterKey();

    @Nonnull
    A create(@Nonnull Context context) throws Exception;
}
