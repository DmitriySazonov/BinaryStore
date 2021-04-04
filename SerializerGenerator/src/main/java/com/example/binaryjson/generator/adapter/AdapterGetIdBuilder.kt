package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName

object AdapterGetIdBuilder {

    private const val GET_ID_METHOD = "id"

    fun generateGetIdMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(GET_ID_METHOD) {
            addStatement("return ${metadata.id}")
            returns(TypeName.INT)
        }
    }
}