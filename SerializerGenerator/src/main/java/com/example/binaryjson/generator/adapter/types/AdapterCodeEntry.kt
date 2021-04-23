package com.example.binaryjson.generator.adapter.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock

interface AdapterCodeEntry {

    interface Context {
        fun adapterName(type: ClassName): String
        fun generateValName(): String
    }

    class ValueName(val name: String)

    sealed class SizePart {
        class Expression(val expression: String) : SizePart()
        class Constant(val size: Int) : SizePart()
    }

    fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: Context,
            builder: CodeBlock.Builder
    )

    fun generateDeserialize(
            bufferName: String,
            context: Context,
            builder: CodeBlock.Builder
    ): ValueName

    fun generateGetSize(
            valueName: String,
            context: Context,
            builder: CodeBlock.Builder
    ): List<SizePart>
}