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
        val element: TypeElement
) {
    val type: TypeName = TypeName.get(element.asType())
}
