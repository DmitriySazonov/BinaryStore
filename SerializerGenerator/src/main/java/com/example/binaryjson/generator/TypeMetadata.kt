package com.example.binaryjson.generator

import com.binarystore.InjectType
import javax.lang.model.element.TypeElement

data class TypeMetadata(
        val id: Int,
        val versionId: Int,
        val injectType: InjectType,
        val fields: List<FieldMetadata>,
        val element: TypeElement
)