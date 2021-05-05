package com.example.binaryjson.generator

import com.binarystore.Array
import com.binarystore.ProvideProperties
import com.binarystore.ProvideProperty
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException

class FieldsCollector(
        private val element: TypeElement,
        private val env: ProcessingEnvironment
) {
    val fields = ArrayList<Field>()

    private val staticTypesHelper = StaticTypesHelper(env)

    fun addField(variable: VariableElement) {
        if (!variable.kind.isField) return
        if (variable.enclosingElement != element) return
        fields += Field(
                name = variable.simpleName.toString(),
                typeMeta = createTypeMeta(variable),
                properties = getProperties(variable)
        )
    }

    private fun getProperties(variable: VariableElement): List<ClassName>? {
        val property = try {
            variable.getAnnotation(ProvideProperty::class.java)
                    ?.property?.java?.let(ClassName::get)
        } catch (e: MirroredTypeException) {
            ClassName.get(e.typeMirror)
        }
        val properties = try {
            variable.getAnnotation(ProvideProperties::class.java)?.properties?.map {
                ClassName.get(it.java)
            }
        } catch (e: MirroredTypesException) {
            e.typeMirrors.map {
                ClassName.get(it) as ClassName
            }
        }
        property ?: properties ?: return null
        return ArrayList<ClassName>().apply {
            if (property is ClassName) add(property)
            properties?.forEach { add(it) }
        }
    }

    private fun createTypeMeta(variable: VariableElement): TypeMeta {
        val name = variable.simpleName.toString()
        val type = ClassName.get(variable.asType())
        val isStaticType = staticTypesHelper.isStaticType(variable)
        return when {
            type.isPrimitive || type.isBoxedPrimitive -> TypeMeta.Primitive(type)
            type is ClassName -> TypeMeta.Class.Simple(type, isStaticType)
            type is ParameterizedTypeName -> TypeMeta.Class.Generic(type, isStaticType)
            type is ArrayTypeName -> {
                val even = variable.getArrayAnnotation()?.even ?: true
                TypeMeta.Array(type, even, isStaticType)
            }
            else -> throw IllegalArgumentException("Unknown filed($name) type($type)")
        }
    }

    private fun VariableElement.getArrayAnnotation(): Array? {
        return getAnnotation(Array::class.java)
    }
}
