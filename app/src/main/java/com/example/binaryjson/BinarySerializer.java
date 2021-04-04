package com.example.binaryjson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BinarySerializer {

    private static final int BUFFER_SIZE = 1024;

    private BinarySerializer() {

    }

    public static byte[] toByteArray(Map<String, Object> map) throws Exception {
        return toByteArrayInternal(map);
    }

    public static byte[] toByteArray(JSONObject jsonObject) throws Exception {
        return toByteArrayInternal(jsonObject);
    }

    private static byte[] toByteArrayInternal(Object object) throws Exception {
        DynamicByteBuffer buffer = new DynamicByteBuffer(BUFFER_SIZE);
        NameMap nameMap = new NameMap();
        buffer.setOffset(DynamicByteBuffer.INTEGER_BYTES);
        storeValue(object, buffer, nameMap);
        final int offset = buffer.getOffset();
        storeNameMap(nameMap, buffer);
        buffer.setOffset(0);
        buffer.write(offset);
        return buffer.getBytes();
    }

    private static void storeMap(Map<String, Object> map, DynamicByteBuffer buffer, NameMap nameMap) throws Exception {
        buffer.write(Tokens.MAP);
        buffer.write(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            buffer.write(nameMap.getIdOrPut(entry.getKey()));
            storeValue(entry.getValue(), buffer, nameMap);
        }
    }

    private static void storeJsonObject(JSONObject object, DynamicByteBuffer buffer, NameMap nameMap) throws Exception {
        buffer.write(Tokens.JSON_OBJECT);
        final Iterator<String> iterator = object.keys();
        buffer.write(object.length());
        String key;
        while (iterator.hasNext()) {
            key = iterator.next();
            buffer.write(nameMap.getIdOrPut(key));
            storeValue(object.get(key), buffer, nameMap);
        }
    }

    private static void storeJsonArray(JSONArray array, DynamicByteBuffer buffer, NameMap nameMap) throws Exception {
        buffer.write(Tokens.JSON_ARRAY);
        buffer.write(array.length());
        for (int i = 0; i < array.length(); i++) {
            storeValue(array.get(i), buffer, nameMap);
        }
    }

    private static void storeSet(Set set, DynamicByteBuffer buffer, NameMap nameMap) throws Exception {
        buffer.write(Tokens.SET);
        buffer.write(set.size());
        for (Object value : set) {
            storeValue(value, buffer, nameMap);
        }
    }

    private static void storeValue(Object object, DynamicByteBuffer buffer, NameMap nameMap) throws Exception {
        if (object instanceof Boolean) {
            buffer.write(Tokens.BOOLEAN);
            buffer.write((Boolean) object);
        } else if (object instanceof Byte) {
            buffer.write(Tokens.BYTE);
            buffer.write((Byte) object);
        } else if (object instanceof Integer) {
            buffer.write(Tokens.INT);
            buffer.write((Integer) object);
        } else if (object instanceof Long) {
            buffer.write(Tokens.LONG);
            buffer.write((Long) object);
        } else if (object instanceof Float) {
            buffer.write(Tokens.FLOAT);
            buffer.write((Float) object);
        } else if (object instanceof Double) {
            buffer.write(Tokens.DOUBLE);
            buffer.write((Double) object);
        } else if (object instanceof String) {
            buffer.write(Tokens.STRING);
            SerializationUtils.putString(buffer, (String) object);
        } else if (object instanceof Set) {
            storeSet((Set) object, buffer, nameMap);
        } else if (object instanceof Map) {
            storeMap((Map) object, buffer, nameMap);
        } else if (object instanceof JSONObject) {
            storeJsonObject((JSONObject) object, buffer, nameMap);
        } else if (object instanceof JSONArray) {
            storeJsonArray((JSONArray) object, buffer, nameMap);
        } else {
            buffer.write(Tokens.NULL);
        }
    }

    private static void storeNameMap(NameMap nameMap, DynamicByteBuffer buffer) {
        Map<String, Integer> names = nameMap.getResultMap();
        buffer.write(names.size());
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            SerializationUtils.putString(buffer, entry.getKey());
            buffer.write(entry.getValue());
        }
    }
}
