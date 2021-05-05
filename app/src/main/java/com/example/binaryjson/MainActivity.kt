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
import com.example.binaryjson.test.MapTest

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MapTest.start()

//        JSONCompareMeasure(this).start()
    }
}
