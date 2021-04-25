package com.example.binaryjson;

import com.binarystore.Field;
import com.binarystore.InjectType;
import com.binarystore.Persistable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Persistable(id = "NameMap", inject = InjectType.ASSIGNMENT)
public class NameMap {

    int lastId = 0;
    Integer lastIdBox = 0;
    @Field(staticType = true)
    Map<String, Integer> nameMap = new TreeMap<>();

    public int getIdOrPut(final String name) {
        final Integer id = nameMap.get(name);
        if (id != null) return id;
        final int newId = lastId++;
        nameMap.put(name, newId);
        return newId;
    }

    public Map<String, Integer> getResultMap() {
        return nameMap;
    }
}
