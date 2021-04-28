package com.example.binaryjson.generator.adapter.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName


internal const val TRUE = (1).toByte()
internal const val FALSE = (0).toByte()

interface TypeCodeGenerator {

    interface Context {
        /**
         * Generate BinaryAdapter field in class for type
         * @return name of generated adapter
         * */
        fun getOrCreateAdapterFieldFor(type: ClassName): String

        /**
         * Generate expression for getting adapter by key
         * @return expression
         * */
        fun generateAdapterByKeyExpression(keyExpression: String): String


        /**
         * Generate expression for getting adapter for class
         * @return expression
         * */
        fun generateAdapterForClassExpression(classExpression: String): String

        fun getAdapterTypeNameFor(className: ClassName): TypeName

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
            builder: CodeBlock.Builder,
    )

    fun generateDeserialize(
            bufferName: String,
            context: Context,
            builder: CodeBlock.Builder,
    ): ValueName

    fun generateGetSize(
            valueName: String,
            context: Context,
            builder: CodeBlock.Builder,
    ): List<SizePart>
}