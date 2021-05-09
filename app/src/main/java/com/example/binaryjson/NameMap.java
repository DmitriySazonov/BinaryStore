package com.example.binaryjson;

import com.binarystore.InjectType;
import com.binarystore.Persistable;
import com.binarystore.ProvideProperties;
import com.binarystore.adapter.map.MapSettings;

import java.util.HashMap;
import java.util.Map;

@Persistable(id = "NameMap", inject = InjectType.AUTO)
public class NameMap {

    int lastId = 0;
    int[] arr;
    Integer lastIdBox = 0;

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

    NameMap(
            int[] arr,
            int lastId
    ) {
        this.lastId = lastId;
    }

    public NameMap() {

    }
}
