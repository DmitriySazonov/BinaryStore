package com.example.binaryjson.test

import com.binarystore.buffer.StaticByteBuffer
import com.binarystore.map.SimpleBinaryLazyMap
import com.example.binaryjson.*
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.compare.ObjectComparator
import java.util.*
import kotlin.collections.HashMap

object MapTest {

    val map = hashMapOf<Any, Any>(
            /*   null to null,
               "test" to "test",
               arrayOf(1, 2) to arrayOf(4, 5),
               1 to 2,
               4f to 5f,
               6.toDouble() to 7.toDouble(),
               'q' to 'r',
               hashMapOf("nested" to "map") to hashMapOf("should" to "work"),*/
            /*TestClass() to NameMap().apply {
                nameMap["sdfsd"] = 2
                nameMap["sdfsd"] = arrayOf(4, 5)
            }*/
    )

    val lazyMap = SimpleBinaryLazyMap<Int, TestClass>(mapOf(
            1 to TestClass(),
            2 to TestClass(),
            3 to TestClass(),
            4 to TestClass(),
            5 to TestClass(),
            6 to TestClass()
    ))

    val enumMap = EnumMap<EnumTest, String>(EnumTest::class.java).apply {
        fillMap()
    }

    val hashMapWIthEnum = HashMap<EnumTest, String>().apply {
        fillMap()
    }

    object CaseSuite : Benchmark.CaseSuite() {
        val SERIALIZE = case("serialize")
        val DESERIALIZE = case("deserialize")
        val GET_SIZE = case("get_size")
    }

    fun start() {

        val map = SimpleBinaryLazyMap<Int, TestClass>(mapOf(
                1 to TestClass(),
                2 to TestClass(),
                3 to TestClass(),
                4 to TestClass(),
                5 to TestClass(),
                6 to TestClass()
        ))
        val benchmark = Benchmark(CaseSuite)
        repeat(1) {
            val provider = createDefaultBinaryAdapterManager()
            provider.register(EnumTest::class.java, EnumTestBinaryAdapter.Factory())
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

    private fun MutableMap<EnumTest, String>.fillMap() {
        EnumTest.values().forEach {
            put(it, it.name)
        }
    }
}