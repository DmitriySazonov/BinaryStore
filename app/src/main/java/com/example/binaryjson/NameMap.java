package com.example.binaryjson;

import com.binarystore.InjectType;
import com.binarystore.annotation.BinaryConstructor;
import com.binarystore.annotation.Persistable;
import com.binarystore.annotation.ProvideProperties;
import com.binarystore.adapter.map.MapSettings;

import java.util.HashMap;
import java.util.Map;

@Persistable(id = "NameMap", inject = InjectType.AUTO)
public class NameMap {

    int lastId = 2;
    int[] arr;
    Integer[] lastIdBox = {};

    @ProvideProperties(properties = {
            MapSettings.SkipItemSettingProperty.class,
    })
    public Map<String, Object> nameMap = new HashMap<>();

    @ProvideProperties(properties = {
            MapSettings.ThrowExceptionSettingProperty.class,
    })
    String russianText = "Привет мир";
    String russianText2 = "Привет мир";
    String russianText3 = "Привет мир";

    Map<String, String> map;

    @BinaryConstructor
    NameMap(
            int[] arr,
            int lastId,
            Map<String, String> map
    ) {
        this.lastId = lastId;
    }

    NameMap(
            int[] arr,
            int lastId,
            String russianText3
    ) {
        this.lastId = lastId;
    }

    public NameMap(int a) {

    }
}
