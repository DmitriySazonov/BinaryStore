package com.example.binaryjson.test

import com.binarystore.buffer.StaticByteBuffer
import com.example.binaryjson.EnumTest
import com.example.binaryjson.TestClass
import com.example.binaryjson.TestClassJava
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.compare.ObjectComparator
import com.example.binaryjson.createDefaultBinaryAdapterManager
import java.util.*

object MapTest {

    val map = mapOf(
            null to null,
            "test" to "test",
            arrayOf(1, 2) to arrayOf(4, 5),
            1 to 2,
            4f to 5f,
            6.toDouble() to 7.toDouble(),
            'q' to 'r',
            hashMapOf("nested" to "map") to hashMapOf("should" to "work"),
            TestClass() to TestClassJava()
    )

    val enumMap = EnumMap<EnumTest, String>(EnumTest::class.java).apply {
        put(EnumTest.ENUM, "enum")
        put(EnumTest.TEST, "test")
    }

    object CaseSuite : Benchmark.CaseSuite() {
        val SERIALIZE = case("serialize")
        val DESERIALIZE = case("deserialize")
        val GET_SIZE = case("get_size")
    }

    fun start() {

        val map = enumMap
        val benchmark = Benchmark(CaseSuite)
        repeat(1) {
            val provider = createDefaultBinaryAdapterManager()
            val adapter = provider.getAdapterForClass(map.javaClass, null)!!
            benchmark.start(CaseSuite.GET_SIZE)
            val size = adapter.getSize(map)
            benchmark.end(CaseSuite.GET_SIZE)
            val buffer = StaticByteBuffer(size)
            benchmark.start(CaseSuite.SERIALIZE)
            adapter.serialize(buffer, map)
            benchmark.end(CaseSuite.SERIALIZE)
            buffer.offset = 0
            benchmark.start(CaseSuite.DESERIALIZE)
            val newMap = adapter.deserialize(buffer)
            benchmark.end(CaseSuite.DESERIALIZE)

            val compare = ObjectComparator.compare(map, newMap)
            compare.toString()
        }
        benchmark.print()
    }
}