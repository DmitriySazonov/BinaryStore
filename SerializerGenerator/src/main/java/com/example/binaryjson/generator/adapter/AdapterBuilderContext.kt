package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.BinaryAdapter
import com.example.binaryjson.generator.AdapterProviderGeneratorHelper
import com.example.binaryjson.generator.InlineExpression
import com.example.binaryjson.generator.PropertiesName
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

data class StaticAdapterEntry(
        val className: ClassName,
        val propertiesName: PropertiesName?
)

class AdapterBuilderContext(
        private val adapterFieldSuffix: String,
        private val adapterProviderField: String
) : TypeCodeGenerator.Context {

    val fieldToProperty = HashMap<String, PropertiesName>()
    val uniqueAdapterTypes = HashSet<StaticAdapterEntry>()

    private val existentValues = HashMap<String, Int>()
    private var varCount = 0

    fun adapterFiledName(adapterEntry: StaticAdapterEntry): String {
        val (type, properties) = adapterEntry
        return "${type.simpleName()}${adapterFieldSuffix}"
                .decapitalize(Locale.getDefault()) + (properties?.run { "_$name" } ?: "")
    }

    override fun getOrCreateAdapterFieldFor(type: ClassName, properties: PropertiesName?): String {
        val entry = StaticAdapterEntry(type, properties)
        uniqueAdapterTypes.add(entry)
        return adapterFiledName(entry)
    }

    override fun generateAdapterByKeyExpression(
            keyExpression: InlineExpression,
            properties: PropertiesName?
    ): String {
        return "${adapterProviderField}.${
            AdapterProviderGeneratorHelper
                    .invoke_getAdapterByKey(keyExpression, properties)
        }"
    }

    override fun generateAdapterForClassExpression(
            classExpression: InlineExpression,
            properties: PropertiesName?
    ): String {
        return "${adapterProviderField}.${
            AdapterProviderGeneratorHelper
                    .invoke_getAdapterForClass(classExpression, properties)
        }"
    }

    override fun getAdapterTypeNameFor(className: ClassName): TypeName {
        return ParameterizedTypeName.get(ClassName.get(BinaryAdapter::class.java), className)
    }

    override fun getUniqueValName(base: String?): String {
        if (base == null) {
            return "var${varCount++}"
        }
        val order = existentValues[base] ?: 0
        existentValues[base] = order + 1
        return if (order == 0) {
            base
        } else {
            "$base$order"
        }
    }
}