package com.example.binaryjson.generator

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

sealed class FieldMeta(
        val name: String,
        val version: Version?
) {
    abstract val type: TypeName

    class Primitive(
            name: String,
            version: Version?,
            override val type: TypeName
    ) : FieldMeta(name, version)

    class Class(
            name: String,
            version: Version?,
            override val type: ClassName
    ) : FieldMeta(name, version)

    class Generic(
            name: String,
            version: Version?,
            override val type: ParameterizedTypeName
    ) : FieldMeta(name, version)

    class Array(
            name: String,
            version: Version?,
            val even: Boolean,
            override val type: ArrayTypeName
    ) : FieldMeta(name, version) {

        val deep: Int
        val baseType: TypeName

        init {
            var type = type.componentType
            var deep = 1
            while (type is ArrayTypeName) {
                type = type.componentType
                deep++
            }
            this.deep = deep
            this.baseType = type
        }

    }

    class Version(id: Int, fallback: String)
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
