package com.example.binaryjson.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement

data class Constructor(
        val params: List<Param>
) {
    data class Param(val name: String, val type: TypeName)
}

class ConstructorCollector {
    val constructors = ArrayList<Constructor>()

    fun addConstructor(executable: ExecutableElement) {
        constructors += Constructor(executable.parameters.map {
            Constructor.Param(
                    name = it.simpleName.toString(),
                    type = ClassName.get(it.asType()).rawType
            )
        })
    }
}