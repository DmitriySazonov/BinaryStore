package com.example.binaryjson.measure

import android.content.Context
import com.example.binaryjson.BinaryParser
import com.example.binaryjson.BinarySerializer
import org.json.JSONArray
import org.json.JSONObject

class JSONMeasure(
        private val context: Context,
        private val runCount: Int
) : Measure {

    override fun run() {
        val json = String(context.assets.open("start_reactions_mock.json").readBytes())

        var bytes: ByteArray? = null
        val jsonTest = JSONObject(json)
        measurePrint("storeToBinary", count = runCount) {
            bytes = BinarySerializer.toByteArray(jsonTest)
        }

        var readObject: JSONObject? = null
        measurePrint("parseBinary", count = runCount) {
            readObject = BinaryParser.parseJSONObject(bytes!!)
        }

        var jsonObject = JSONObject()
        measurePrint("jsonDefaultParse", count = runCount) {
            jsonObject = JSONObject(json)
        }

        var toString: String? = null
        measurePrint("jsonToString", count = runCount) {
            toString = jsonObject.toString()
        }

        log("isEquals = ${compare(jsonObject, readObject!!)}")
    }

    private fun compare(orig: JSONObject, json: JSONObject?): Boolean {
        return orig.keys().asSequence().all { key ->
            when (val value = orig[key]) {
                is JSONObject -> compare(value, json?.optJSONObject(key))
                is JSONArray -> compare(value, json?.optJSONArray(key))
                else -> value == json?.opt(key)
            }
        }
    }

    private fun compare(orig: JSONArray, array: JSONArray?): Boolean {
        return (0 until orig.length()).all {
            when (val value = orig[it]) {
                is JSONObject -> compare(value, array?.optJSONObject(it))
                is JSONArray -> compare(value, array?.optJSONArray(it))
                else -> value == array?.opt(it)
            }
        }
    }
}