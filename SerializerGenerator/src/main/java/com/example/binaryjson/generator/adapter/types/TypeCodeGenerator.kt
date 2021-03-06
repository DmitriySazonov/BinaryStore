package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName


interface TypeCodeGenerator {

    interface Context {
        /**
         * Generate BinaryAdapter field in class for type
         * @return name of generated adapter
         * */
        fun getOrCreateAdapterFieldFor(type: ClassName, properties: PropertiesName?): String

        /**
         * Generate expression for getting adapter by key
         * @return expression
         * */
        fun generateAdapterByKeyExpression(
                keyExpression: InlineExpression,
                properties: PropertiesName?
        ): String


        /**
         * Generate expression for getting adapter for class
         * @return expression
         * */
        fun generateAdapterForClassExpression(
                classExpression: InlineExpression,
                properties: PropertiesName?
        ): String

        fun getAdapterTypeNameFor(className: ClassName): TypeName

        fun getUniqueValName(base: String? = null): String
    }

    sealed class SizePart {
        class Expression(val expression: String) : SizePart()
        class Constant(val size: Int) : SizePart()
    }

    sealed class Variable(val name: String) {
        class Unexcited(name: String) : Variable(name)
        class Assignable(name: String) : Variable(name)
    }

    inline class DeserializeResult(val expression: String)

    fun generateSerialize(
            value: ValueName,
            buffer: BufferName,
            properties: PropertiesName?,
            context: Context,
            builder: CodeBlock.Builder,
    )

    fun generateDeserialize(
            buffer: BufferName,
            variable: Variable,
            properties: PropertiesName?,
            context: Context,
            builder: CodeBlock.Builder,
    ): DeserializeResult

    fun generateGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: Context,
            builder: CodeBlock.Builder,
    ): List<SizePart>
}