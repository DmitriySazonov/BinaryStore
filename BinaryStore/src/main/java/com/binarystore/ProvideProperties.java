package com.binarystore;

import com.binarystore.dependency.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
// @Repeatable() Need make it repeatable when java will be 8
public @interface ProvideProperties {
    Class<? extends Property<?>>[] properties();
}
