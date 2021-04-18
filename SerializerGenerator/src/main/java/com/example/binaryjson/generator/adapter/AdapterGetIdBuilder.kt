package com.example.binaryjson.generator.adapter

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec

object AdapterGetIdBuilder : AdapterCodeBuilder {

    private const val GET_ID_METHOD = "id"

    override fun TypeSpec.Builder.build(context: AdapterCodeBuilder.Context) {
        addMethod(generateGetIdMethod(context))
    }

    private fun generateGetIdMethod(context: AdapterCodeBuilder.Context): MethodSpec {
        return adapterMethod(GET_ID_METHOD) {
            addStatement("return ${context.idStaticFiledName}")
            returns(context.metadata.id.keyClass)
        }
    }
}
