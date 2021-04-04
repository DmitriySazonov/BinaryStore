package com.example.binaryjson;

import java.util.HashMap;
import java.util.Map;

public class NameMap {

    private int lastId = 0;
    private HashMap<String, Integer> nameMap = new HashMap<>();

    public NameMap() {

    }

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
