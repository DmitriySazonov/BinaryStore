package com.example.binaryjson.generator

import com.binarystore.InjectType
import com.squareup.javapoet.TypeName
import javax.lang.model.element.TypeElement

sealed class Id {
    data class Int(val value: kotlin.Int) : Id()
    data class String(val value: kotlin.String) : Id()
}

data class TypeMetadata(
        val id: Id,
        val versionId: Int,
        val injectType: InjectType,
        val fields: List<Field>,
        val element: TypeElement,
        val constructors: List<Constructor>
) {
    val type: TypeName = TypeName.get(element.asType())
    val appropriatesConstructors = findAllAppropriateConstructors()

    fun findMostAppropriateConstructor(): Constructor? {
        if (constructors.isEmpty()) {
            return Constructor(emptyList())
        }
        return appropriatesConstructors.maxByOrNull(::percentOfFit)
    }

    fun findFullMatchConstructor(): Constructor? {
        return appropriatesConstructors.firstOrNull { percentOfFit(it) == 1f }
    }

    private fun findAllAppropriateConstructors(): List<Constructor> {
        val fieldsAsParams = fields.map {
            Constructor.Param(it.name, it.typeMeta.type)
        }.toSet()
        return constructors.filter { constructor ->
            constructor.params.all { it in fieldsAsParams }
        }
    }

    private fun percentOfFit(constructor: Constructor): Float {
        if (fields.isEmpty()) {
            return if (constructor.params.isEmpty()) 1f else 0f
        }
        val fieldsAsParams = fields.map {
            Constructor.Param(it.name, it.typeMeta.type)
        }.toMutableSet()
        constructor.params.forEach(fieldsAsParams::remove)

        return 1f - (fieldsAsParams.size / fields.size.toFloat())
    }
}
