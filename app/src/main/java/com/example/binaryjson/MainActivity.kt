package com.example.binaryjson

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.binaryjson.measure.JSONCompareMeasure
import com.example.binaryjson.test.CollectionTest
import com.example.binaryjson.test.MapTest

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MapTest.start()
        CollectionTest.start()


        findViewById<View>(R.id.testRun).setOnClickListener {
            Thread({
                JSONCompareMeasure(this).start()
            }, "JSONCompareMeasure").start()
        }
    }
}
