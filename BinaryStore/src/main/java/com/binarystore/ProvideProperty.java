package com.binarystore;

import com.binarystore.dependency.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface ProvideProperty {
    Class<? extends Property<?>> property();
}
