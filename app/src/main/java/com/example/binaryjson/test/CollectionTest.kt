package com.example.binaryjson.test

import android.util.Log
import com.binarystore.adapter.collection.common.queues.QueueBinaryAdapter
import com.binarystore.buffer.StaticByteBuffer
import com.example.binaryjson.TestClass
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.compare.ObjectComparator
import com.example.binaryjson.createDefaultBinaryAdapterManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

object CollectionTest {

    private val emptyData: Collection<Any> by lazy {
        emptyList<Any>()
    }
    private val singleItemData: Collection<Any> by lazy {
        listOf(createTestObject())
    }
    private val manyItemData: Collection<Any> by lazy {
        mutableListOf<Any>().apply {
            repeat(100) {
                add(createTestObject())
            }
        }
    }

    private var currentTestData: Collection<Any> = manyItemData
    private val testCases = listOf<Collection<Any>>(
            ArrayList(currentTestData),
            LinkedList(currentTestData),
            Stack<Any>().apply {
                currentTestData.forEach { push(it) }
            },
            Vector(currentTestData),
            ArrayDeque(currentTestData),
            HashSet(currentTestData),
            LinkedHashSet(currentTestData),
            //PriorityQueue(currentTestData)
            //TreeSet(currentTestData)
    )


    object CaseSuite : Benchmark.CaseSuite() {
        val SERIALIZE = CaseSuite.case("serialize")
        val DESERIALIZE = CaseSuite.case("deserialize")
        val GET_SIZE = CaseSuite.case("get_size")
    }

    fun start() {
        testCases.forEach {
            val collection = it
            val benchmark = Benchmark(CaseSuite)
            repeat(1) { _ ->
                Log.d("Benchmark", "check ${it.javaClass}")
                val provider = createDefaultBinaryAdapterManager()
                val adapter = provider.getAdapterForClass(collection.javaClass, null)!!
                benchmark.start(CaseSuite.GET_SIZE)
                val size = adapter.getSize(collection)
                benchmark.end(CaseSuite.GET_SIZE)
                val buffer = StaticByteBuffer(size)
                benchmark.start(CaseSuite.SERIALIZE)
                adapter.serialize(buffer, collection)
                benchmark.end(CaseSuite.SERIALIZE)
                buffer.offset = 0
                benchmark.start(CaseSuite.DESERIALIZE)
                val newCollection = adapter.deserialize(buffer)
                benchmark.end(CaseSuite.DESERIALIZE)

                val compare = ObjectComparator.compare(collection, newCollection)
                compare.toString()
            }
            benchmark.print()
        }
    }
     private fun createTestObject() : Any {
         return TestClass()
     }

}
