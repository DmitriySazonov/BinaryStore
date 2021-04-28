package com.example.binaryjson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binarystore.AdaptersRegistrator
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.buffer.StaticByteBuffer
import com.binarystore.meta.MetadataStoreInMemory
import com.example.binaryjson.compare.ObjectComparator
import com.example.binaryjson.measure.JSONCompareMeasure

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val metadataStore = MetadataStoreInMemory()
        val provider = BinaryAdapterManager(metadataStore).apply {
            BasicBinaryAdapters.registerInto(this)
            AdaptersRegistrator.registerInto(this)
        }
        val adapter = provider.getAdapterForClass(NameMap::class.java)!!
        val value = NameMap()
        value.lastId = 4
        repeat(10) {
            value.nameMap["key - $it"] = it
        }
        val size = adapter.getSize(value)
        val buffer = StaticByteBuffer(size)
        adapter.serialize(buffer, value)
        buffer.offset = 0
        val newValue = adapter.deserialize(buffer)

        val compare = ObjectComparator.compare(value, newValue)
        compare.toString()

//        JSONCompareMeasure(this).start()
    }
}
