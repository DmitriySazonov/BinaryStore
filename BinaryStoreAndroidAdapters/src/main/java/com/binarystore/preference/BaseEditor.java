package com.binarystore.preference;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;

public abstract class BaseEditor<Self extends BaseEditor<?>> implements SharedPreferences.Editor {

    protected Map<String, Object> editable = new HashMap<>();
    protected boolean wasClear = false;

    @SuppressWarnings("unchecked")
    Self replaceAll(final Map<String, Object> newValues) {
        editable = newValues;
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putString(String key, @CheckForNull String value) {
        editable.put(key, value);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putStringSet(String key, @CheckForNull Set<String> values) {
        editable.put(key, values);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putInt(String key, int value) {
        editable.put(key, value);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putLong(String key, long value) {
        editable.put(key, value);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putFloat(String key, float value) {
        editable.put(key, value);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self putBoolean(String key, boolean value) {
        editable.put(key, value);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self remove(String key) {
        editable.put(key, null);
        return (Self) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Self clear() {
        editable.clear();
        wasClear = true;
        return (Self) this;
    }
}
