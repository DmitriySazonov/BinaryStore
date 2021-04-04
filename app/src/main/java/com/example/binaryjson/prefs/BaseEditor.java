package com.example.binaryjson.prefs;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseEditor implements SharedPreferences.Editor {

    protected Map<String, Object> editable = new HashMap<>();
    protected boolean wasClear = false;

    @Override
    public synchronized SharedPreferences.Editor putString(String key, @Nullable String value) {
        editable.put(key, value);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
        editable.put(key, values);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor putInt(String key, int value) {
        editable.put(key, value);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor putLong(String key, long value) {
        editable.put(key, value);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor putFloat(String key, float value) {
        editable.put(key, value);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor putBoolean(String key, boolean value) {
        editable.put(key, value);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor remove(String key) {
        editable.put(key, null);
        return this;
    }

    @Override
    public synchronized SharedPreferences.Editor clear() {
        editable.clear();
        wasClear = true;
        return this;
    }
}
