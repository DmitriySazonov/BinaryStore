package com.example.binaryjson.test

import com.binarystore.buffer.StaticByteBuffer
import com.example.binaryjson.TestClass
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.compare.ObjectComparator
import com.example.binaryjson.createDefaultBinaryAdapterManager

object CollectionTest {

    val list = arrayListOf(TestClass())

    object CaseSuite : Benchmark.CaseSuite() {
        val SERIALIZE = CaseSuite.case("serialize")
        val DESERIALIZE = CaseSuite.case("deserialize")
        val GET_SIZE = CaseSuite.case("get_size")
    }

    fun start() {

        val list = list
        val benchmark = Benchmark(CaseSuite)
        repeat(1) {
            val provider = createDefaultBinaryAdapterManager()
            val adapter = provider.getAdapterForClass(list.javaClass, null)!!
            benchmark.start(CaseSuite.GET_SIZE)
            val size = adapter.getSize(list)
            benchmark.end(CaseSuite.GET_SIZE)
            val buffer = StaticByteBuffer(size)
            benchmark.start(CaseSuite.SERIALIZE)
            adapter.serialize(buffer, list)
            benchmark.end(CaseSuite.SERIALIZE)
            buffer.offset = 0
            benchmark.start(CaseSuite.DESERIALIZE)
            val newMap = adapter.deserialize(buffer)
            benchmark.end(CaseSuite.DESERIALIZE)

            val compare = ObjectComparator.compare(list, newMap)
            compare.toString()
        }
        benchmark.print()
    }

}