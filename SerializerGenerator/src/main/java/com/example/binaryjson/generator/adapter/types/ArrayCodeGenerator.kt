package com.example.binaryjson.generator.adapter.types

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.TypeMeta
import com.example.binaryjson.generator.adapter.forEach
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator.SizePart
import com.squareup.javapoet.CodeBlock

class ArrayCodeGenerator(
        private val typeMeta: TypeMeta.Array,
        private val factory: TypeCodeGeneratorFactory,
) : TypeCodeGenerator {

    private val baseType = typeMeta.baseTypeMeta.type
    private val baseTypeMeta = typeMeta.baseTypeMeta
    private val deep = typeMeta.deep

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.checkForNullAndWrite(valueName, bufferName) {
            forEach(valueName, typeMeta.type, beforeFor = {
                addStatement("${bufferName}.write(${it}.length)")
            }) {
                factory.create(baseTypeMeta).generateSerialize(it, bufferName, context, builder)
            }
        }
    }

    override fun generateDeserialize(
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.ValueName {
        val returnName = context.generateValName()
        val arrayDefine = (0 until deep).joinToString("") { "[]" }
        val arrayDimension = { deep: Int ->
            (0 until deep).joinToString("") {
                if (it == 0) "[${bufferName}.readInt()]" else "[]"
            }
        }
        var deep = deep
        builder.addStatement("\$T$arrayDefine $returnName", baseType)

        builder.checkForNullInBuffer(bufferName, nonnullCode = {
            forEach(returnName, typeMeta.type, beforeFor = {
                addStatement("$it = new \$T${arrayDimension(deep--)}", baseType)
            }) {
                val value = factory.create(baseTypeMeta)
                        .generateDeserialize(bufferName, context, builder)
                addStatement("$it = ${value.name}")
            }
        }, nullCode = {
            addStatement("$returnName = null")
        })
        return TypeCodeGenerator.ValueName(returnName)
    }

    override fun generateGetSize(
            valueName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<SizePart> {
        val accumulatorName = context.generateValName()
        builder.addStatement("int $accumulatorName = 0")
        builder.checkForNull(valueName, nonnullCode = {
            if (typeMeta.even && typeMeta.baseTypeMeta is TypeMeta.Primitive) {
                generateGetSizeCodePrimitiveEvenArray(valueName, accumulatorName, builder)
            } else {
                generateGetSizeCodeArray(valueName, accumulatorName, context, builder)
            }
        })
        return listOf(
                SizePart.Expression(accumulatorName),
                SizePart.Constant(CHECK_FOR_NULL_SIZE)
        )
    }

    private fun generateGetSizeCodeArray(
            valueName: String,
            accumulatorName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.apply {
            forEach(valueName, typeMeta.type) {
                val parts = factory.create(baseTypeMeta)
                        .generateGetSize(it, context, builder)
                parts.forEach { part ->
                    val expression = when (part) {
                        is SizePart.Constant -> "${part.size}"
                        is SizePart.Expression -> part.expression
                    }
                    addStatement("$accumulatorName += $expression")
                }
            }
            addStatement("$accumulatorName += ${ByteBuffer.INTEGER_BYTES * deep}")
        }
    }

    private fun generateGetSizeCodePrimitiveEvenArray(
            valueName: String,
            accumulatorName: String,
            builder: CodeBlock.Builder
    ) {
        val size = baseType.getPrimitiveSize()
        val arrayDimension = { it: Int ->
            (0 until it).joinToString("") { "[0]" }
        }
        var expression = "$size"
        repeat(deep) {
            expression += " * ${valueName}${arrayDimension(it)}.length"
        }
        builder.addStatement("$accumulatorName += $expression + ${ByteBuffer.INTEGER_BYTES * deep}")
    }
}