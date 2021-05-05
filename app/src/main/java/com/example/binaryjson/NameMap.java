package com.example.binaryjson;

import com.binarystore.InjectType;
import com.binarystore.Persistable;
import com.binarystore.ProvideProperties;
import com.binarystore.ProvideProperty;

import java.util.HashMap;
import java.util.Map;

@Persistable(id = "NameMap", inject = InjectType.ASSIGNMENT)
public class NameMap {

    int lastId = 0;
    int[] arr;
    Integer lastIdBox = 0;

    @ProvideProperties(properties = {SkipItemMapSettingProperty.class})
    Map<String, Object> nameMap = new HashMap<>();
    String russianText = "Привет мир";
}
