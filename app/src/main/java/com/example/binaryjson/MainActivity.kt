package com.example.binaryjson

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.binaryjson.measure.JSONCompareMeasure

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        MapTest.start()

        findViewById<View>(R.id.testRun).setOnClickListener {
            Thread({
                JSONCompareMeasure(this).start()
            }, "JSONCompareMeasure").start()
        }
    }
}
