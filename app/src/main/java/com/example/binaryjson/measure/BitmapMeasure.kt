package com.example.binaryjson.measure

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import com.binarystore.buffer.StaticByteBuffer
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.createDefaultBinaryAdapterManager
import kotlin.random.Random

/*
 0 - serialize - 13.37829702970297
 1 - deserialize - 1.1024158415841583
* */

object BitmapMeasure {

    object CaseSuite : Benchmark.CaseSuite() {
        val SERIALIZE = case("serialize")
        val DESERIALIZE = case("deserialize")
    }

    fun start() {
        val benchmark = Benchmark(CaseSuite)

        val bitmap = bitmapFromBase64(320, 160)
        measure(benchmark, bitmap)
        repeat(100) {
            measure(benchmark, bitmap)
        }
        benchmark.print()
    }

    private fun measure(benchmark: Benchmark, bitmap: Bitmap) {
        val adapter = createDefaultBinaryAdapterManager().getAdapterForClass(Bitmap::class.java,
                null)!!

        val buffer = StaticByteBuffer(adapter.getSize(bitmap))
        benchmark.start(CaseSuite.SERIALIZE)
        adapter.serialize(buffer, bitmap)
        benchmark.end(CaseSuite.SERIALIZE)

        buffer.offset = 0

        benchmark.start(CaseSuite.DESERIALIZE)
        val deserializeBitmap = adapter.deserialize(buffer)
        benchmark.end(CaseSuite.DESERIALIZE)

        val equals = equals(bitmap, deserializeBitmap)
        equals.toString()
    }

    private fun equals(bitmap1: Bitmap, bitmap2: Bitmap): Boolean {
        return (0 until bitmap1.width).all { x ->
            (0 until bitmap1.height).all { y ->
                if (bitmap1.getPixel(x, y) == bitmap2.getPixel(x, y)) {
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun bitmapFromBase64(width: Int, height: Int): Bitmap {
        val str = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAUDBAQEAwUEBAQFBQUGBwwIBwcHBw8LCwkMEQ8SEhEPERETFhwXExQaFRERGCEYGh0dHx8fExciJCIeJBweHx7/2wBDAQUFBQcGBw4ICA4eFBEUHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh7/wAARCAASACADASIAAhEBAxEB/8QAGAABAQEBAQAAAAAAAAAAAAAABgAHCAX/xAAjEAABAwMEAgMAAAAAAAAAAAABAAIDBAURBhIhMSJRFEGR/8QAGAEAAgMAAAAAAAAAAAAAAAAABAUAAQb/xAAcEQACAgIDAAAAAAAAAAAAAAAAAQIDITEEETL/2gAMAwEAAhEDEQA/AOYY4XZ4aUu0nbXykbmHCT2vQcheN0Z/ElZYhaoc7MEBKnaamjjdvIOvllDKUkN+kEqqcxSOB9rQdR3UeURd0g9e9sjyQopMu+EU8HVtHGzjwb16R7W4xG7HHClIZaDa9swPU7j8x/J7K8QE+1KRK0LrvbP/2Q=="
        return Base64.decode(str, Base64.DEFAULT).let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }.let {
            Bitmap.createScaledBitmap(it, width, height, true)
        }
    }

    private fun generateBitmap(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            (0 until width).forEach { x ->
                (0 until height).forEach { y ->
                    setPixel(x, y, Color.rgb(
                            Random.nextInt(),
                            Random.nextInt(),
                            Random.nextInt(),
                    ))
                }
            }

            /*Canvas(this).apply {
                drawColor(Color.GRAY)
                drawText("Hello", 50f, 50f, Paint().apply {
                    textSize = 64f
                    style = Paint.Style.FILL
                    color = Color.BLUE
                })
            }*/
        }
    }
}