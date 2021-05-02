package com.example.binaryjson.generator

import com.binarystore.Array
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class FieldsCollector(
        private val element: TypeElement,
        private val env: ProcessingEnvironment
) {
    val fields = ArrayList<Field>()

    private val staticTypesHelper = StaticTypesHelper(env)

    fun addField(variable: VariableElement) {
        if (!variable.kind.isField) return
        if (variable.enclosingElement != element) return
        fields += Field(variable.simpleName.toString(), createTypeMeta(variable))
    }

    private fun createTypeMeta(variable: VariableElement): TypeMeta {
        val name = variable.simpleName.toString()
        val type = ClassName.get(variable.asType())
        staticTypesHelper.isStaticType(variable)
        return when {
            type.isPrimitive || type.isBoxedPrimitive -> TypeMeta.Primitive(type)
            type is ClassName -> TypeMeta.Class.Simple(type,
                    staticTypesHelper.isStaticType(variable))
            type is ParameterizedTypeName -> TypeMeta.Class.Generic(type,
                    staticTypesHelper.isStaticType(variable))
            type is ArrayTypeName -> {
                val even = variable.getArrayAnnotation()?.even ?: true
                TypeMeta.Array(type, even)
            }
            else -> throw IllegalArgumentException("Unknown filed($name) type($type)")
        }
    }

    private fun VariableElement.getArrayAnnotation(): Array? {
        return getAnnotation(Array::class.java)
    }
}
