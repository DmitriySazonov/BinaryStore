package com.binarystore.dependency;

public final class PropertiesUtils {

    public static <T> T getOrDefault(Properties properties, Class<T> tClass, T def) {
        T dep = properties.get(tClass, null);
        return dep != null ? dep : def;
    }
}
