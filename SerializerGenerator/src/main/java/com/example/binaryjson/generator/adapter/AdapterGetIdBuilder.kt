package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

object AdapterGetIdBuilder : CodeBuilder {

    private const val GET_ID_METHOD = "id"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addMethod(generateGetIdMethod(context.metadata))
    }

    private fun generateGetIdMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(GET_ID_METHOD) {
            addStatement("return ${metadata.id}")
            returns(TypeName.INT)
        }
    }
}
