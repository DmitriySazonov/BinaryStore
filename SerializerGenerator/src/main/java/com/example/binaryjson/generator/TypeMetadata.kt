package com.example.binaryjson.generator

import com.binarystore.InjectType
import com.squareup.javapoet.TypeName
import javax.lang.model.element.TypeElement

data class TypeMetadata(
        val id: Int,
        val versionId: Int,
        val injectType: InjectType,
        val fields: List<FieldMeta>,
        val element: TypeElement
) {
    val type: TypeName = TypeName.get(element.asType())
}
