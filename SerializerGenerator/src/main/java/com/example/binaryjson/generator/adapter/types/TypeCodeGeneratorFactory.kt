package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.TypeMeta

object TypeCodeGeneratorFactory {

    fun create(type: TypeMeta): TypeCodeGenerator {
        return when (type) {
            is TypeMeta.Array -> ArrayCodeGenerator(type, this)
            is TypeMeta.Class -> ClassCodeGenerator(type)
            is TypeMeta.Primitive -> PrimitiveCodeGenerator(type)
        }
    }
}