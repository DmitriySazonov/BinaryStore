package com.example.binaryjson.generator.adapter.types

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.adapter.forEach
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator.SizePart
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator.Variable
import com.squareup.javapoet.CodeBlock

class ArrayContext(
        val outerBuilder: CodeBlock.Builder,
        context: TypeCodeGenerator.Context
) : TypeCodeGenerator.Context by context

private const val useArrayContext = true

class ArrayCodeGenerator(
        private val typeMeta: TypeMeta.Array,
        private val factory: TypeCodeGeneratorFactory,
) : TypeCodeGenerator {

    private val baseType = typeMeta.baseTypeMeta.type
    private val baseTypeMeta = typeMeta.baseTypeMeta
    private val deep = typeMeta.deep

    override fun generateSerialize(
            value: ValueName,
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.checkForNullAndWrite(value, buffer) {
            val arrayContext = if (useArrayContext) ArrayContext(builder, context) else context
            val innerCodeBuilder = CodeBlock.builder().apply {
                forEach(value.name, typeMeta.type, beforeFor = {
                    addStatement(BufferGeneratorHelper.invoke_write(buffer,
                            ValueName("${it}.length")))
                }) {
                    factory.create(baseTypeMeta).generateSerialize(ValueName(it), buffer,
                            properties, arrayContext, this)
                }
            }
            builder.add(innerCodeBuilder.build())
        }
    }

    override fun generateDeserialize(
            buffer: BufferName,
            variable: Variable,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.DeserializeResult {
        val returnName = variable.name
        val arrayDefine = (0 until deep).joinToString("") { "[]" }
        val arrayDimension = { deep: Int ->
            (0 until deep).joinToString("") {
                if (it == 0) "[${buffer.name}.readInt()]" else "[]"
            }
        }
        var deep = deep

        if (variable is Variable.Unexcited) {
            builder.addStatement("\$T$arrayDefine $returnName", baseType)
        }

        builder.checkForNullInBuffer(buffer, nonnullCode = {
            val arrayContext = if (useArrayContext) ArrayContext(this, context) else context
            val innerCodeBuilder = CodeBlock.builder().apply {
                this.forEach(returnName, typeMeta.type, beforeFor = {
                    this.addStatement("$it = new \$T${arrayDimension(deep--)}", baseType)
                }) {
                    factory.create(baseTypeMeta).generateDeserialize(buffer,
                            Variable.Assignable(it), properties, arrayContext, this)
                }
            }
            add(innerCodeBuilder.build())
        }, nullCode = {
            addStatement("$returnName = null")
        })
        return TypeCodeGenerator.DeserializeResult(returnName)
    }

    override fun generateGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<SizePart> {
        builder.checkForNull(value, nonnullCode = {
            val arrayContext = if (useArrayContext) ArrayContext(builder, context) else context
            val innerCodeBuilder = CodeBlock.builder().apply {
                if (typeMeta.even && typeMeta.baseTypeMeta is TypeMeta.Primitive) {
                    generateGetSizeCodePrimitiveEvenArray(value, accumulator, this)
                } else {
                    generateGetSizeCodeArray(value, properties, accumulator, arrayContext, this)
                }
            }
            builder.add(innerCodeBuilder.build())
        })
        return listOf(
                SizePart.Constant(CHECK_FOR_NULL_SIZE)
        )
    }

    private fun generateGetSizeCodeArray(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.apply {
            forEach(value.name, typeMeta.type) {
                val parts = factory.create(baseTypeMeta)
                        .generateGetSize(ValueName(it), properties,
                                accumulator, context, builder)
                parts.forEach { part ->
                    val expression = when (part) {
                        is SizePart.Constant -> "${part.size}"
                        is SizePart.Expression -> part.expression
                    }
                    addStatement("${accumulator.name} += $expression")
                }
            }
            addStatement("${accumulator.name} += ${ByteBuffer.INTEGER_BYTES * deep}")
        }
    }

    private fun generateGetSizeCodePrimitiveEvenArray(
            value: ValueName,
            accumulator: AccumulatorName,
            builder: CodeBlock.Builder
    ) {
        val size = baseType.getPrimitiveSize()
        val arrayDimension = { it: Int ->
            (0 until it).joinToString("") { "[0]" }
        }
        var expression = "$size"
        repeat(deep) {
            expression += " * ${value.name}${arrayDimension(it)}.length"
        }
        builder.addStatement("${accumulator.name} += $expression + ${ByteBuffer.INTEGER_BYTES * deep}")
    }
}