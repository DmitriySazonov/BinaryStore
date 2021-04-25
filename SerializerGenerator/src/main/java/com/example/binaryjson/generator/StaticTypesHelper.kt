package com.example.binaryjson.generator

import com.squareup.javapoet.ClassName
import javax.lang.model.element.VariableElement

object StaticTypesHelper {

    private val staticTypes = setOf(
            java.lang.String::class.java.name,
    )

    fun isStaticType(variable: VariableElement): Boolean {
        val type = ClassName.get(variable.asType())
        val field = variable.getAnnotation(com.binarystore.Field::class.java)
        return type.isPrimitive || type.isBoxedPrimitive || field?.staticType == true
                || type.toString() in staticTypes
    }
}