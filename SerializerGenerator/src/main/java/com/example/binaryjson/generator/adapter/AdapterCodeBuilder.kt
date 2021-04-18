package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec

interface AdapterCodeBuilder {

    interface Context {
        val adapterClassName: ClassName
        val metadata: TypeMetadata

        val idStaticFiledName: String
    }

    fun TypeSpec.Builder.build(context: Context) = Unit
}
