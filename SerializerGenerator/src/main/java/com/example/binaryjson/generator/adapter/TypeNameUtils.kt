package com.example.binaryjson.generator.adapter

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.FieldMeta
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

fun TypeName.getPrimitiveSize(): Int {
    return when (toString()) {
        "boolean" -> ByteBuffer.BOOLEAN_BYTES
        "byte" -> ByteBuffer.BYTE_BYTES
        "short" -> ByteBuffer.SHORT_BYTES
        "int" -> ByteBuffer.INTEGER_BYTES
        "long" -> ByteBuffer.LONG_BYTES
        "float" -> ByteBuffer.FLOAT_BYTES
        "double" -> ByteBuffer.DOUBLE_BYTES
        else -> throw IllegalArgumentException("unknown primitive type $this")
    }
}

val ArrayTypeName.baseType: TypeName
    get() {
        var type = componentType
        while (type is ArrayTypeName) {
            type = type.componentType
        }
        return type
    }

val ArrayTypeName.deep: Int
    get() {
        var type = componentType
        var deep = 1
        while (type is ArrayTypeName) {
            type = type.componentType
            deep++
        }
        return deep
    }

fun findUniqueTypes(fields: List<FieldMeta>): List<ClassName> {
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