package com.example.binaryjson.generator.adapter

import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec

interface CodeBuilder {

    interface Context {
        val typeClass: ClassName
        val metadata: TypeMetadata
    }

    fun TypeSpec.Builder.build(context: Context) = Unit
}
