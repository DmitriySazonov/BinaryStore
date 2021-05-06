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


class AdapterBuilderContext(
        private val adapterFieldSuffix: String,
        private val adapterProviderField: String
) : TypeCodeGenerator.Context {

    val fieldToProperty = HashMap<String, PropertiesName>()
    val uniqueAdapterTypes = HashSet<ClassName>()
    private val valueName = "var"
    private var valueOrder = 0

    fun adapterFiledName(type: ClassName): String {
        return "${type.simpleName()}${adapterFieldSuffix}"
                .decapitalize(Locale.getDefault())
    }

    override fun getOrCreateAdapterFieldFor(type: ClassName): String {
        uniqueAdapterTypes.add(type)
        return adapterFiledName(type)
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

    override fun generateValName(): String {
        return "$valueName${valueOrder++}"
    }
}