package com.binarystore.collections;

import java.util.Collection;

interface BinaryLazyList<T> extends Iterable<T>, Collection<T> {

    int size();

    T get(int index) throws Exception;
}
