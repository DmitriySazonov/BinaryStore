package com.example.binaryjson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.adapter.ByteBuffer
import com.binarystore.meta.MetadataStoreInMemory
import com.binarystore.register
import com.example.binaryjson.measure.JSONMeasure
import com.example.binaryjson.measure.Measure
import com.example.binaryjson.measure.SharedPrefsMeasure
import com.example.binaryjson.widget.PaintView

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
        setContentView(PaintView(this))

        val metadataStore = MetadataStoreInMemory()
        val provider = BinaryAdapterManager(metadataStore).apply {
            BasicBinaryAdapters.registerInto(this)
            register(TestClass::class.java, ::TestClassBinaryAdapter)
            register(TestClassJava::class.java, ::TestClassJavaBinaryAdapter)
        }
        val adapter = provider.getAdapter(TestClass::class.java)
        val testClass = TestClass("Hello", "world")
        val size = adapter.getSize(testClass)
        val byteBuffer = ByteBuffer(size)
        adapter.serialize(byteBuffer, testClass)
        byteBuffer.offset = 0
        val desTestClass = adapter.deserialize(byteBuffer)
        desTestClass.toString()

        SerializationUtils.test()

//        setContentView(R.layout.activity_main)

        /* findViewById<View>(R.id.testRun).setOnClickListener {
             measures.forEach { it.run() }
         }*/
    }
}