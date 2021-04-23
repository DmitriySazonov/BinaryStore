package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.FieldMeta
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

object AdapterCodeEntryFactory {

    fun create(type: TypeName): AdapterCodeEntry {
        return when {
            type is ArrayTypeName -> ArrayCodeEntry(type, false, this)
            type is ParameterizedTypeName -> ClassCodeEntry(type.rawType)
            type is ClassName -> ClassCodeEntry(type)
            type.isPrimitive -> PrimitiveCodeEntry(type)
            else -> throw IllegalArgumentException("Unknown type $type")
        }
    }

    fun create(field: FieldMeta): AdapterCodeEntry {
        return when (field) {
            is FieldMeta.Array -> ArrayCodeEntry(field.type, field.even, this)
            is FieldMeta.Class -> ClassCodeEntry(field.type)
            is FieldMeta.Generic -> ClassCodeEntry(field.type.rawType)
            is FieldMeta.Primitive -> PrimitiveCodeEntry(field.type)
        }
    }
}