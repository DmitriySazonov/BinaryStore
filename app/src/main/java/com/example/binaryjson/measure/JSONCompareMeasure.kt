package com.example.binaryjson.measure

import android.content.Context
import com.binarystore.buffer.StaticByteBuffer
import com.example.binaryjson.benchmark.Benchmark
import com.example.binaryjson.compare.ObjectComparator
import com.example.binaryjson.createDefaultBinaryAdapterManager
import com.example.binaryjson.test.StoryResponse
import com.example.binaryjson.test.StoryResponseParser
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class JSONCompareMeasure(context: Context) {

    object CompareCaseSuite : Benchmark.CaseSuite() {
        val CONFIGURE = case("configure")
        val GET_ADAPTER = case("get_adapter")
        val GET_SIZE = case("get_size")
        val CREATE_BUFFER = case("create_buffer")
        val SERIALIZE = case("serialize")
        val DESERIALIZE = case("deserialize")

        val ITERATE = case("iterate")

        val READ_JSON = case("read_json")
        val CREATE_JSON_OBJ = case("create_json_obj")
        val PARSE_FROM_JSON = case("parse_from_json")
        val TO_BYTE_ARRAY = case("to_byte_array")
    }

    private val json = String(context.assets.open("get_stories.json").readBytes())

    fun start() {
        test(Benchmark(CompareCaseSuite)) // warm up
        val benchmark = Benchmark(CompareCaseSuite)
        val count = 100
        repeat(count) { test(benchmark) }
        benchmark.print()
    }

    private fun test(benchmark: Benchmark) {
        benchmark.start(CompareCaseSuite.CONFIGURE)
        val provider = createDefaultBinaryAdapterManager()
        benchmark.end(CompareCaseSuite.CONFIGURE)

        benchmark.start(CompareCaseSuite.CREATE_JSON_OBJ)
        val json = JSONObject(json)
        benchmark.end(CompareCaseSuite.CREATE_JSON_OBJ)

        benchmark.start(CompareCaseSuite.PARSE_FROM_JSON)
        val response = StoryResponseParser.parse(json)
        benchmark.end(CompareCaseSuite.PARSE_FROM_JSON)

        benchmark.start(CompareCaseSuite.GET_ADAPTER)
        val adapter = provider.getAdapterForClass(StoryResponse::class.java, null)!!
        benchmark.end(CompareCaseSuite.GET_ADAPTER)

        benchmark.start(CompareCaseSuite.GET_SIZE)
        val size = adapter.getSize(response)
        benchmark.end(CompareCaseSuite.GET_SIZE)

        benchmark.start(CompareCaseSuite.CREATE_BUFFER)
        val buffer = StaticByteBuffer(size)
        benchmark.end(CompareCaseSuite.CREATE_BUFFER)

        benchmark.start(CompareCaseSuite.SERIALIZE)
        adapter.serialize(buffer, response)
        benchmark.end(CompareCaseSuite.SERIALIZE)

        buffer.offset = 0

        benchmark.start(CompareCaseSuite.DESERIALIZE)
        val desResponse = adapter.deserialize(buffer)
        benchmark.end(CompareCaseSuite.DESERIALIZE)


        /*   benchmark.start(CompareCaseSuite.ITERATE)
           desResponse.items.forEach {
               it.stories.forEach {

               }
           }
           benchmark.end(CompareCaseSuite.ITERATE)*/

        benchmark.start(CompareCaseSuite.TO_BYTE_ARRAY)
        String(this.json.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
        benchmark.end(CompareCaseSuite.TO_BYTE_ARRAY)

        val compare = ObjectComparator.compare(response, desResponse)
        desResponse.toString()
    }
}