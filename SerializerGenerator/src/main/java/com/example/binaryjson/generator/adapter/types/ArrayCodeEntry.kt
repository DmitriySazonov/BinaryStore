package com.example.binaryjson.generator.adapter.types

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.adapter.baseType
import com.example.binaryjson.generator.adapter.deep
import com.example.binaryjson.generator.adapter.forEach
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.example.binaryjson.generator.adapter.types.AdapterCodeEntry.SizePart
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.CodeBlock

class ArrayCodeEntry(
        private val type: ArrayTypeName,
        private val event: Boolean,
        private val factory: AdapterCodeEntryFactory,
) : AdapterCodeEntry {

    private val baseType = type.baseType
    private val deep = type.deep

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.apply {
            forEach(valueName, type, beforeFor = {
                addStatement("${bufferName}.write(${it}.length)")
            }) {
                factory.create(baseType).generateSerialize(it, bufferName, context, builder)
            }
        }
    }

    override fun generateDeserialize(
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): AdapterCodeEntry.ValueName {
        val returnName = context.generateValName()
        builder.apply {
            val arrayDefine = (0 until deep).joinToString("") { "[]" }
            val arrayDimension = { deep: Int ->
                (0 until deep).joinToString("") {
                    if (it == 0) "[${bufferName}.readInt()]" else "[]"
                }
            }
            addStatement("\$T$arrayDefine $returnName", baseType)
            var deep = deep
            forEach(returnName, type, beforeFor = {
                addStatement("$it = new \$T${arrayDimension(deep--)}", baseType)
            }) {
                val value = factory.create(baseType)
                        .generateDeserialize(bufferName, context, builder)
                addStatement("$it = ${value.name}")
            }
        }
        return AdapterCodeEntry.ValueName(returnName)
    }

    override fun generateGetSize(
            valueName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): List<SizePart> {
        return if (event && baseType.isPrimitive) {
            generateGetSizeCodePrimitiveEvenArray(valueName, context)
        } else {
            generateGetSizeCodeArray(valueName, context, builder)
        }
    }

    private fun generateGetSizeCodeArray(
            valueName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): List<SizePart> {
        val accumulator = context.generateValName()
        builder.apply {
            addStatement("int $accumulator = 0")
            forEach(valueName, type) {
                val parts = factory.create(baseType)
                        .generateGetSize(it, context, builder)
                parts.forEach { part ->
                    val expression = when (part) {
                        is SizePart.Constant -> "${part.size}"
                        is SizePart.Expression -> part.expression
                    }
                    addStatement("$accumulator += $expression")
                }
            }
        }
        return listOf(
                SizePart.Expression(accumulator),
                SizePart.Constant(ByteBuffer.INTEGER_BYTES * deep)
        )
    }

    private fun generateGetSizeCodePrimitiveEvenArray(
            valueName: String,
            context: AdapterCodeEntry.Context,
    ): List<SizePart> {
        val size = baseType.getPrimitiveSize()
        val arrayDimension = { it: Int ->
            (0 until it).joinToString("") { "[0]" }
        }
        var expression = "$size"
        repeat(deep) {
            expression += " * ${valueName}${arrayDimension(it)}.length"
        }
        return listOf(
                SizePart.Expression(expression),
                SizePart.Constant(ByteBuffer.INTEGER_BYTES * deep)
        )
    }
}