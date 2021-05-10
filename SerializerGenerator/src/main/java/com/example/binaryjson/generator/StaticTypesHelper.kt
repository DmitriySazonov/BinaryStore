package com.example.binaryjson.generator

import com.binarystore.annotation.Field
import com.example.binaryjson.generator.visitors.CanBeStaticTypeDetector
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

class StaticTypesHelper(
        private val env: ProcessingEnvironment,
) {

    private val staticTypes = setOf(
            java.lang.String::class.java.name,
    )
    private val canBeStaticTypeDetector = CanBeStaticTypeDetector(env)

    fun isStaticType(variable: VariableElement): Boolean {
        val type = ClassName.get(variable.asType())
        val field = variable.getFieldAnnotation()
        return type.isPrimitive || type.isBoxedPrimitive || field?.staticType == true
                || type.toString() in staticTypes
    }

    private fun VariableElement.getFieldAnnotation(): Field? {
        val field = getAnnotation(Field::class.java) ?: return null

        if (field.staticType && !asType().canBeStaticType) {
            throw IllegalArgumentException("Only class can be marked as a static " +
                    "type but it isn't class. ${asType()}")
        }
        return field
    }

    private val TypeMirror.canBeStaticType: Boolean
        get() = accept(canBeStaticTypeDetector, null) ?: false
}