package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.Key
import com.example.binaryjson.generator.Id
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec

object AdapterGetIdBuilder : CodeBuilder {

    private const val GET_ID_METHOD = "id"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addMethod(generateGetIdMethod(context.metadata))
    }

    private fun generateGetIdMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(GET_ID_METHOD) {
            metadata.id.generateReturnCode(this)
        }
    }
}
