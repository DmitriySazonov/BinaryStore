package com.example.binaryjson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.AbstractAdapterFactory
import com.binarystore.adapter.AdapterFactory
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.adapter.BinaryAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metadataStore = MetadataStoreInMemory()
        val provider = BinaryAdapterManager(metadataStore).apply {
            BasicBinaryAdapters.registerInto(this)
            register(TestClass::class.java, object : AbstractAdapterFactory<TestClass>(2) {
                override fun create(context: AdapterFactory.Context): BinaryAdapter<TestClass> {
                    return TestClassBinaryAdapter(context.provider, context.metadataStore)
                }
            })
            register(TestClassJava::class.java, object : AbstractAdapterFactory<TestClassJava>(3) {
                override fun create(context: AdapterFactory.Context): BinaryAdapter<TestClassJava> {
                    return TestClassJavaBinaryAdapter(context.provider, context.metadataStore)
                }
            })
        }
        provider.resolveAllAdapters()
        val adapter = provider.getAdapter(TestClass::class.java) ?: return
        val testClass = TestClass("Hello", "world")
        val size = adapter.getSize(testClass)
        val byteBuffer = DynamicByteBuffer(size)
        adapter.serialize(byteBuffer, testClass)
        byteBuffer.offset = 0
        val desTestClass = adapter.deserialize(byteBuffer)
        desTestClass.toString()
    }
}
