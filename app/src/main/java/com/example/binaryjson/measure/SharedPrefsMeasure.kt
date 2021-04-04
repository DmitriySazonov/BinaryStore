package com.example.binaryjson.measure

import android.content.Context
import android.content.SharedPreferences
import com.example.binaryjson.prefs.SharedPreferenceManager
import java.util.concurrent.Executors

class SharedPrefsMeasure(
        private val context: Context,
        private val runCount: Int,
        private val recordCount: Int,
        private val isCommit: Boolean
) : Measure {

    override fun run() {
        val service = Executors.newSingleThreadExecutor()
        val prefsManager = SharedPreferenceManager(service, true)
        testSharedPref("binary") {
            prefsManager.create(context, "sharedPref$it")
        }
        Thread.sleep(1000)
        testSharedPref("standard") {
            context.getSharedPreferences("default$it", Context.MODE_PRIVATE)
        }

        val isEquals = compare(prefsManager.create(context, "sharedPref0"),
                context.getSharedPreferences("default0", Context.MODE_PRIVATE))

        log("isEquals = $isEquals")
    }

    private fun compare(pref1: SharedPreferences, pref2: SharedPreferences): Boolean {
        val map1 = pref1.all
        val map2 = pref2.all
        if (map1.size != map2.size) return false

        map1.forEach { (key, value) ->
            map2[key]?.equals(value) ?: return false
            map2.remove(key)
        }

        return map2.isEmpty()
    }

    private fun testSharedPref(name: String, prefsFactory: (Int) -> SharedPreferences) {
        measurePrint("sharedPref:read:$name", count = runCount) {
            prefsFactory(it).getString("key_$it", "fail")
        }

        measurePrint("sharedPref:write:$name", count = runCount) {
            val pref = prefsFactory(it)
            val editor = pref.edit()
            editor.clear()
            repeat(recordCount) {
                editor.putString("key_$it", "Привет_$it")
            }
            if (isCommit) editor.commit() else editor.apply()
        }
    }
}