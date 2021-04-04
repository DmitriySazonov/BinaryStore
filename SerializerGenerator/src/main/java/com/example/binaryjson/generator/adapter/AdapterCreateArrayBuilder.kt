package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName

object AdapterCreateArrayBuilder {

    private const val CREATE_ARRAY_METHOD = "createArray"
    private const val SIZE_PARAM_NAME = "size"

    fun generateMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(CREATE_ARRAY_METHOD) {
            addParameter(TypeName.INT, SIZE_PARAM_NAME)
            addStatement("return new \$T[$SIZE_PARAM_NAME]", metadata.element)

            val typeName = TypeName.get(metadata.element.asType())
            returns(ArrayTypeName.of(typeName))
        }
    }
}