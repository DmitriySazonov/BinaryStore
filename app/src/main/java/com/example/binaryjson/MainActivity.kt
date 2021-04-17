package com.example.binaryjson

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.buffer.StaticByteBuffer
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
        private val diffs = HashMap<String, Long>()

        fun start(name: String) {
            values[name] = SystemClock.elapsedRealtimeNanos()
        }

        fun end(name: String) {
            val time = values[name] ?: return
            val diff = diffs[name] ?: 0
            diffs[name] = diff + (SystemClock.elapsedRealtimeNanos() - time)
        }

        fun print(count: Int, name: String? = null) {
            diffs.filter {
                name == null || it.key == name
            }.forEach { (name, time) ->
                Log.d("Benchmark", "$id - $name - ${time / count.toDouble()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val benchmark = Benchmark("")
        val count = 1000
        repeat(count) {
            test(benchmark)
        }
        benchmark.print(count)
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
        val testClass = TestClass("Напишу ка я что-то по русски", "world")
        benchmark.start("getSize")
        val size = adapter.getSize(testClass)
        benchmark.end("getSize")
        benchmark.start("createBuffer")
        val byteBuffer = StaticByteBuffer(size)
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
