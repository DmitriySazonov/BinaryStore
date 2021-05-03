package com.example.binaryjson.measure

import com.binarystore.AdaptersRegistrator
import com.binarystore.BinaryAdapterManager
import com.binarystore.adapter.BasicBinaryAdapters
import com.binarystore.buffer.StaticByteBuffer
import com.binarystore.meta.MetadataStoreInMemory
import com.example.binaryjson.TestClass
import com.example.binaryjson.benchmark.Benchmark

object FullFlowMeasure {

    object FullFlowCaseSuite : Benchmark.CaseSuite() {
        val CONFIGURE = case("configure")
        val RESOLVE = case("resolve")
        val GET_ADAPTER = case("get_adapter")
        val GET_SIZE = case("get_size")
        val CREATE_BUFFER = case("create_buffer")
        val SERIALIZE = case("serialize")
        val DESERIALIZE = case("deserialize")
    }

    fun start() {
        val benchmark = Benchmark(FullFlowCaseSuite)
        val count = 1000
        repeat(count) { test(benchmark) }
        benchmark.print()
    }

    private fun test(benchmark: Benchmark) {

        benchmark.start(FullFlowCaseSuite.CONFIGURE)
        val metadataStore = MetadataStoreInMemory()
        val provider = BinaryAdapterManager(metadataStore).apply {
            BasicBinaryAdapters.registerInto(this)
            AdaptersRegistrator.registerInto(this)
        }
        benchmark.end(FullFlowCaseSuite.CONFIGURE)
        benchmark.start(FullFlowCaseSuite.RESOLVE)
        provider.resolveAllAdapters()
        benchmark.end(FullFlowCaseSuite.RESOLVE)
        benchmark.start(FullFlowCaseSuite.GET_ADAPTER)
        val adapter = provider.getAdapterForClass(TestClass::class.java, null)!!
        benchmark.end(FullFlowCaseSuite.GET_ADAPTER)
        val testClass = TestClass("Напишу ка я что-то по русски", "world")
        benchmark.start(FullFlowCaseSuite.GET_SIZE)
        val size = adapter.getSize(testClass)
        benchmark.end(FullFlowCaseSuite.GET_SIZE)
        benchmark.start(FullFlowCaseSuite.CREATE_BUFFER)
        val byteBuffer = StaticByteBuffer(size)
        benchmark.end(FullFlowCaseSuite.CREATE_BUFFER)
        benchmark.start(FullFlowCaseSuite.SERIALIZE)
        adapter.serialize(byteBuffer, testClass)
        benchmark.end(FullFlowCaseSuite.SERIALIZE)
        byteBuffer.offset = 0
        benchmark.start(FullFlowCaseSuite.DESERIALIZE)
        val desTestClass = adapter.deserialize(byteBuffer)
        benchmark.end(FullFlowCaseSuite.DESERIALIZE)
        desTestClass.toString()
    }
}