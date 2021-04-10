package com.example.binaryjson.generator

import com.binarystore.Array
import com.binarystore.Version
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class FieldsCollector(
        private val element: TypeElement
) {
    val fields = ArrayList<FieldMeta>()

    fun addField(variable: VariableElement) {
        if (!variable.kind.isField) return
        if (variable.enclosingElement != element) return
        fields += createFiledMeta(variable)
    }

    private fun createFiledMeta(variable: VariableElement): FieldMeta {
        val name = variable.simpleName.toString()
        val type = ClassName.get(variable.asType())
        val version = variable.getVersion()
        return when {
            type.isPrimitive -> FieldMeta.Primitive(name, version, type)
            type is ClassName -> FieldMeta.Class(name, version, type)
            type is ParameterizedTypeName -> FieldMeta.Generic(name, version, type)
            type is ArrayTypeName -> {
                val even = variable.getArrayAnnotation()?.even ?: true
                FieldMeta.Array(name, version, even, type)
            }
            else -> throw IllegalArgumentException("Unknown filed($name) type($type)")
        }
    }

    private fun VariableElement.getArrayAnnotation(): Array? {
        return getAnnotation(Array::class.java)
    }

    private fun VariableElement.getVersion(): FieldMeta.Version? {
        return getAnnotation(Version::class.java)?.toVersionMeta()
    }

    private fun Version.toVersionMeta(): FieldMeta.Version {
        return FieldMeta.Version(
                id = id,
                fallback = fallback
        )
    }
}
