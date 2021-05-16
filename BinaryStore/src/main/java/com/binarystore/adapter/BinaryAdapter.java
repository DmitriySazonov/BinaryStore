package com.binarystore.adapter;

import javax.annotation.Nonnull;

public interface BinaryAdapter<T> extends BinarySerializer<T>, BinaryDeserializer<T> {

    @Nonnull
    Key<?> key();
}
