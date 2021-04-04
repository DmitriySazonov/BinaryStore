package com.binarystore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Persistable {
    int id();

    int versionId() default 1;

    InjectType inject() default InjectType.CONSTRUCTOR;
}
