package com.binarystore.collections;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class AbstractBinaryLazyList<T> extends AbstractList<T> {


    @Override
    public void replaceAll(UnaryOperator<T> unaryOperator) {

    }

    @Override
    public void sort(Comparator<? super T> comparator) {

    }

    @Override
    public boolean removeIf(Predicate<? super T> predicate) {
        return false;
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {

    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    @Override
    public Stream<T> stream() {
        return null;
    }

    @Override
    public Stream<T> parallelStream() {
        return null;
    }
}
