package com.example.binaryjson;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BinaryParser {

    private BinaryParser() {

    }

    public static Map<String, Object> parseMap(byte[] bytes) throws Exception {
        final DynamicByteBufferDep buffer = new DynamicByteBufferDep(bytes);
        final String[] names = parseNamesArray(buffer);
        if (buffer.readByte() != Tokens.MAP)
            throw new IllegalArgumentException("Root object isn't a Map");
        return parseMap(buffer, names);
    }

    public static JSONObject parseJSONObject(byte[] bytes) throws Exception {
        final DynamicByteBufferDep buffer = new DynamicByteBufferDep(bytes);
        final String[] names = parseNamesArray(buffer);
        if (buffer.readByte() != Tokens.JSON_OBJECT)
            throw new IllegalArgumentException("Root object isn't a JSONObject");
        return parseJSONObject(buffer, names);
    }

    private static Object parseValue(DynamicByteBufferDep buffer, String[] names) throws Exception {
        final byte token = buffer.readByte();
        if (token == Tokens.BOOLEAN)
            return buffer.readBoolean();
        if (token == Tokens.BYTE)
            return buffer.readByte();
        if (token == Tokens.INT)
            return buffer.readInt();
        if (token == Tokens.LONG)
            return buffer.readLong();
        if (token == Tokens.FLOAT)
            return buffer.readFloat();
        if (token == Tokens.DOUBLE)
            return buffer.readDouble();
        if (token == Tokens.STRING)
            return SerializationUtils.getString(buffer);
        if (token == Tokens.SET)
            return parseSet(buffer, names);
        if (token == Tokens.MAP)
            return parseMap(buffer, names);
        if (token == Tokens.JSON_OBJECT)
            return parseJSONObject(buffer, names);
        if (token == Tokens.JSON_ARRAY)
            return parseJSONArray(buffer, names);
        if (token == Tokens.NULL)
            return null;
        return null;
    }

    private static Map<String, Object> parseMap(DynamicByteBufferDep buffer, String[] names) throws Exception {
        final Map<String, Object> values = new HashMap<>();
        final int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            values.put(
                    names[buffer.readInt()],
                    parseValue(buffer, names)
            );
        }
        return values;
    }

    private static Set<Object> parseSet(DynamicByteBufferDep buffer, String[] names) throws Exception {
        final int count = buffer.readInt();
        final Set<Object> values = new HashSet<>();
        for (int i = 0; i < count; i++) {
            values.add(parseValue(buffer, names));
        }
        return values;
    }

    @SuppressLint("Assert")
    private static JSONObject parseJSONObject(DynamicByteBufferDep buffer, String[] names) throws Exception {
        final JSONObject values = new JSONObject();
        final int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            values.put(
                    names[buffer.readInt()],
                    parseValue(buffer, names)
            );
        }
        return values;
    }

    @SuppressLint("Assert")
    private static JSONArray parseJSONArray(DynamicByteBufferDep buffer, String[] names) throws Exception {
        final int count = buffer.readInt();
        final JSONArray values = new JSONArray();
        for (int i = 0; i < count; i++) {
            values.put(parseValue(buffer, names));
        }
        return values;
    }

    private static String[] parseNamesArray(DynamicByteBufferDep buffer) throws Exception {
        final int offset = buffer.readInt();
        buffer.setOffset(offset);
        final int size = buffer.readInt();
        final String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            final String name = SerializationUtils.getString(buffer);
            final int position = buffer.readInt();
            names[position] = name;
        }
        buffer.setOffset(DynamicByteBufferDep.INTEGER_BYTES);
        return names;
    }
}
