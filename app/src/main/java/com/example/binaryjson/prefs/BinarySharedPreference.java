package com.example.binaryjson.prefs;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.binaryjson.BinaryParser;
import com.example.binaryjson.BinarySerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class BinarySharedPreference implements SharedPreferences {

    private final File file;
    private final ExecutorService executorService;
    private final Map<String, Object> map;
    private WriteOnDiskRunnable writeOnDiskRunnable;

    public BinarySharedPreference(File file, ExecutorService executorService) {
        this(file, readFromFile(file), executorService);
    }

    public BinarySharedPreference(File file, Map<String, Object> values, ExecutorService executorService) {
        this.file = file;
        this.map = values;
        this.executorService = executorService;
    }

    public void applyChanges() {
        synchronized (this) {
            writeOnDiskRunnable = new WriteOnDiskRunnable();
            executorService.submit(writeOnDiskRunnable);
        }
    }

    @Override
    public Map<String, ?> getAll() {
        return map;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return getOrDefault(key, String.class, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return getOrDefault(key, Set.class, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return getOrDefault(key, Integer.class, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return getOrDefault(key, Long.class, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return getOrDefault(key, Float.class, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return getOrDefault(key, Boolean.class, defValue);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public Editor edit() {
        return new BinaryEditor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // TODO implement later
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        // TODO implement later
    }

    private class WriteOnDiskRunnable implements Runnable {

        private boolean canceled = false;

        @Override
        public synchronized void run() {
            if (canceled) return;
            writeToFile(map, file);
        }

        public synchronized void cancel() {
            canceled = true;
        }
    }

    private <T> T getOrDefault(String name, Class<T> tClass, T defaultValue) {
        synchronized (map) {
            Object value = map.get(name);
            return tClass.isInstance(value) ? tClass.cast(value) : defaultValue;
        }
    }

    private void commitInMemory(Map<String, Object> editable, boolean wasClear) {
        synchronized (map) {
            if (wasClear) {
                map.clear();
            }
            for (Map.Entry<String, Object> entry : editable.entrySet()) {
                final Object value = entry.getValue();
                if (value != null) {
                    map.put(entry.getKey(), value);
                } else {
                    map.remove(entry.getKey());
                }
            }
        }
    }

    private boolean writeToFile(Map<String, Object> map, File file) {
        synchronized (this) {
            writeOnDiskRunnable = null;
        }
        try {
            OutputStream stream = new FileOutputStream(file);
            stream.write(BinarySerializer.toByteArray(map));
            stream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Map<String, Object> readFromFile(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            return BinaryParser.parseMap(bytes);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private class BinaryEditor extends BaseEditor {

        @Override
        public synchronized boolean commit() {
            commitInMemory(editable, wasClear);
            return writeToFile(map, file);
        }

        @Override
        public synchronized void apply() {
            synchronized (BinarySharedPreference.this) {
                commitInMemory(editable, wasClear);
                writeOnDiskRunnable = new WriteOnDiskRunnable();
                executorService.submit(writeOnDiskRunnable);
            }
        }
    }
}
