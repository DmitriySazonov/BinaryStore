package com.example.binaryjson.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SharedPreferenceManager {

    private static SharedPreferenceManager instance;

    public static void setInstance(SharedPreferenceManager manager) {
        instance = manager;
    }

    public static SharedPreferenceManager getInstance() {
        return instance;
    }

    private final boolean useBinary;
    private final ExecutorService executorService;
    private Map<String, BinarySharedPreference> preferenceCache = new HashMap<>();

    public SharedPreferenceManager(ExecutorService executorService, boolean useBinary) {
        this.useBinary = useBinary;
        this.executorService = executorService;
    }

    public SharedPreferences create(Context context, String name) {
        if (useBinary) {
            return getBinarySharedPreference(context, name);
        } else {
            return getDefaultSharedPreference(context, name);
        }
    }

    public boolean awaitSaving(long millis) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(millis, TimeUnit.MILLISECONDS);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    private SharedPreferences getDefaultSharedPreference(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private SharedPreferences getBinarySharedPreference(Context context, String name) {
        BinarySharedPreference prefs = preferenceCache.get(name);
        if (prefs == null) {
            prefs = createBinarySharedPreference(context, name);
            preferenceCache.put(name, prefs);
        }
        return prefs;
    }

    private BinarySharedPreference createBinarySharedPreference(Context context, String name) {
        File binaryFile = getFilePath(context, name);
        if (binaryFile.exists()) {
            return new BinarySharedPreference(binaryFile, executorService);
        }
        final SharedPreferences defaultPrefs = getDefaultSharedPreference(context, name);
        BinarySharedPreference binaryPreference = new BinarySharedPreference(binaryFile,
                (Map<String, Object>) defaultPrefs.getAll(), executorService);
        binaryPreference.applyChanges();
        return binaryPreference;
    }

    private File getFilePath(Context context, String fileName) {
        return new File(context.getCacheDir(), fileName);
    }
}
