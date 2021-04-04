package com.example.binaryjson.measure

import android.util.Log
import kotlin.system.measureTimeMillis

interface Measure {
    fun run()

    fun log(message: String) {
        Log.d("MeasureTest", message)
    }

    fun measurePrint(name: String, count: Int, printSteps: Boolean = false, action: (Int) -> Unit) {
        var allTime = 0L
        repeat(count) { number ->
            measureTimeMillis {
                action(number)
            }.also {
                if (printSteps)
                    log("$name:$number - $it")
                if (number == 0) return@also
                allTime += it
            }
        }
        log("$name:average - ${allTime / (count - 1).toFloat()}")
    }
}