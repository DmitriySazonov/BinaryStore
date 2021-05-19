package com.example.binaryjson.benchmark

import android.os.SystemClock
import android.util.Log
import java.util.concurrent.TimeUnit

class Benchmark(
        private val caseSuite: CaseSuite
) {
    abstract class CaseSuite {

        val size: Int get() = names.size
        private val names = ArrayList<String>()

        fun name(index: Int): String? {
            return names.getOrNull(index)
        }

        protected fun case(name: String): Int {
            names += name
            return names.size - 1
        }
    }

    private val measureCount = IntArray(caseSuite.size)
    private val startValues = LongArray(caseSuite.size)
    private val diffs = LongArray(caseSuite.size)

    fun start(id: Int) {
        startValues[id] = SystemClock.elapsedRealtimeNanos()
    }

    fun end(id: Int) {
        val time = startValues[id]
        if (time == 0L) return
        diffs[id] += (SystemClock.elapsedRealtimeNanos() - time)
        measureCount[id]++
        startValues[id] = 0L
    }

    fun print(id: Int? = null) {
        if (id != null) {
            printMeasureResult(id)
            return
        }
        repeat(diffs.size, ::printMeasureResult)
    }

    fun printSum(id: Int? = null) {
        if (id != null) {
            printSumMeasureResult(id)
            return
        }
        repeat(diffs.size, ::printSumMeasureResult)
    }

    private fun printMeasureResult(index: Int) {
        val name = caseSuite.name(index)
        if (measureCount[index] == 0) {
            Log.d("Benchmark", "$index - $name - haven't been measured yet")
            return
        }
        val time = (diffs[index] / measureCount[index].toDouble()) / TimeUnit.MILLISECONDS.toNanos(1)
        Log.d("Benchmark", "$index - $name - $time")
    }

    private fun printSumMeasureResult(index: Int) {
        val name = caseSuite.name(index)
        if (measureCount[index] == 0) {
            Log.d("Benchmark", "$index - $name - haven't been measured yet")
            return
        }
        val time = diffs[index] / TimeUnit.MILLISECONDS.toNanos(1)
        Log.d("Benchmark", "$index - $name - $time")
    }
}