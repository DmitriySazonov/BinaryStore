package com.example.binaryjson;

import com.binarystore.InjectType;
import com.binarystore.Persistable;

import java.util.HashMap;
import java.util.Map;

@Persistable(id = "NameMap", inject = InjectType.ASSIGNMENT)
public class NameMap {

    int lastId = 0;
    HashMap<String, Integer> nameMap = new HashMap<>();

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
