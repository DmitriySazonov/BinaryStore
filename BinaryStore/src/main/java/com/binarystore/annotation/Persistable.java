package com.binarystore.annotation;

import com.binarystore.IdType;
import com.binarystore.InjectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Persistable {
    String id();

    IdType idType() default IdType.STRING;

    int versionId() default 1;

    InjectType inject() default InjectType.CONSTRUCTOR;
}
