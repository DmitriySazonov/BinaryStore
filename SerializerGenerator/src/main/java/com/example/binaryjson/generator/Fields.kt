package com.example.binaryjson.generator

import com.example.binaryjson.generator.adapter.deep
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

class Field(
        val name: String,
        val typeMeta: TypeMeta,
)

sealed class TypeMeta {

    abstract val type: TypeName

    class Primitive(
            override val type: TypeName,
    ) : TypeMeta()

    sealed class Class(
            override val type: ClassName,
            val staticType: Boolean,
    ) : TypeMeta() {

        class Simple(
                type: ClassName,
                staticType: Boolean = false,
        ) : Class(type, staticType)

        class Generic(
                val parameterizedType: ParameterizedTypeName,
                staticType: Boolean = false,
        ) : Class(parameterizedType.rawType, staticType)
    }

    class Array(
            override val type: ArrayTypeName,
            val even: Boolean,
            val baseTypeMeta: TypeMeta = type.baseType.toMeta(),
    ) : TypeMeta() {
        val deep = type.deep
    }
}

private fun TypeName.toMeta(): TypeMeta {
    return when {
        this is ClassName -> TypeMeta.Class.Simple(this)
        this is ParameterizedTypeName -> TypeMeta.Class.Generic(this)
        this is ArrayTypeName -> TypeMeta.Array(this, even = false)
        isPrimitive -> TypeMeta.Primitive(this)
        else -> throw IllegalArgumentException("Unknown type $this")
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

val TypeName.simpleName: String
    get() = when (this) {
        is ParameterizedTypeName -> rawType.simpleName()
        is ClassName -> simpleName()
        is ArrayTypeName -> baseType.simpleName
        else -> toString()
    }
