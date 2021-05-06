package com.example.binaryjson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.binaryjson.test.MapTest

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MapTest.start()

//        JSONCompareMeasure(this).start()
    }
}
