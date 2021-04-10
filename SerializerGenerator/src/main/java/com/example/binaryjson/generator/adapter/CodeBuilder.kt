package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.FieldMeta
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

interface CodeBuilder {

    fun createFields(metadata: TypeMetadata): List<FieldSpec> = emptyList()
    fun createMethods(metadata: TypeMetadata): List<MethodSpec> = emptyList()

    fun requiredAdapters(metadata: TypeMetadata): List<ClassName> {
        return findUniqueTypes(metadata.fields)
    }

    private fun findUniqueTypes(fields: List<FieldMeta>): List<ClassName> {
        return fields.mapNotNull {
            tryGetClassName(it.type)
        }.toSet().toList()
    }

    private fun tryGetClassName(type: TypeName): ClassName? {
        if (type is ClassName)
            return type
        return when (type) {
            is ArrayTypeName -> type.componentType
            is ParameterizedTypeName -> type.rawType
            else -> null
        }?.let(::tryGetClassName)
    }
}
