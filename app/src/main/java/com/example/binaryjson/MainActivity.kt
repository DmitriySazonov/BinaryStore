package com.example.binaryjson

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.buffer.DynamicByteBuffer
import com.binarystore.meta.MetadataStoreInMemory
import com.example.binaryjson.measure.JSONMeasure
import com.example.binaryjson.measure.Measure
import com.example.binaryjson.measure.SharedPrefsMeasure

class MainActivity : AppCompatActivity() {

    private val measures: List<Measure> = listOf(
            JSONMeasure(this,
                    runCount = 100
            ),
            SharedPrefsMeasure(
                    context = this,
                    runCount = 100,
                    recordCount = 1000,
                    isCommit = true
            )
    )

    class Benchmark(val id: String) {

        private val values = HashMap<String, Long>()

        fun start(name: String) {
            values[name] = SystemClock.elapsedRealtime()
        }

        fun end(name: String) {
            val time = values[name] ?: return
            val diff = SystemClock.elapsedRealtime() - time
            Log.d("Benchmark", "$id - $name - $diff")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repeat(10) {
            test(Benchmark(it.toString()))
        }
    }

    private fun test(benchmark: Benchmark) {

        benchmark.start("configure")
        val metadataStore = MetadataStoreInMemory()
        val provider = BinaryAdapterManager(metadataStore).apply {
            BasicBinaryAdapters.registerInto(this)
            register(TestClass::class.java, TestClassBinaryAdapter.Factory())
            register(TestClassJava::class.java, TestClassJavaBinaryAdapter.Factory())
        }
        benchmark.end("configure")
        benchmark.start("resolve")
        provider.resolveAllAdapters()
        benchmark.end("resolve")
        benchmark.start("getProvider")
        val adapter = provider.getAdapter(TestClass::class.java) ?: return
        benchmark.end("getProvider")
        val testClass = TestClass("Hello", "world")
        benchmark.start("getSize")
        val size = adapter.getSize(testClass)
        benchmark.end("getSize")
        benchmark.start("createBuffer")
        val byteBuffer = DynamicByteBuffer(size)
        benchmark.end("createBuffer")
        benchmark.start("serialize")
        adapter.serialize(byteBuffer, testClass)
        benchmark.end("serialize")
        byteBuffer.offset = 0
        benchmark.start("deserialize")
        val desTestClass = adapter.deserialize(byteBuffer)
        benchmark.end("deserialize")
        desTestClass.toString()
    }
}
