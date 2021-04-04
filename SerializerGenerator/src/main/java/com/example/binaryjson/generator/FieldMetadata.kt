package com.example.binaryjson.generator

import com.squareup.javapoet.TypeName

data class FieldMetadata(
        val name: String,
        val type: TypeName,
        val version: Version?
) {
    class Version(id: Int, fallback: String)
}