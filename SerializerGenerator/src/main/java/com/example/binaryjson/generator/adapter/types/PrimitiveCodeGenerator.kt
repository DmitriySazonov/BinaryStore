package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.squareup.javapoet.CodeBlock

class PrimitiveCodeGenerator(
        typeMeta: TypeMeta.Primitive,
) : TypeCodeGenerator {

    private val type = typeMeta.type
    private val isBoxed = typeMeta.type.isBoxedPrimitive

    override fun generateSerialize(
            value: ValueName,
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        val invokeWrite = BufferGeneratorHelper.invoke_write(buffer, value)
        if (isBoxed) {
            builder.checkForNullAndWrite(value, buffer) {
                builder.addStatement(invokeWrite)
            }
        } else {
            builder.addStatement(invokeWrite)
        }
    }

    override fun generateDeserialize(
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.DeserializeResult {
        val valueName = context.getUniqueValName()
        val primitiveType = type.unbox()
        val deserializeCode = BufferGeneratorHelper.invoke_readByType(buffer, primitiveType)
        if (isBoxed) {
            builder.addStatement("final \$T $valueName", type)
            builder.checkForNullInBuffer(buffer, nonnullCode = {
                addStatement("$valueName = $deserializeCode")
            }, nullCode = {
                addStatement("$valueName = null")
            })
        } else {
            builder.addStatement("final $type $valueName = $deserializeCode")
        }
        return TypeCodeGenerator.DeserializeResult(valueName)
    }

    override fun generateGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<TypeCodeGenerator.SizePart> {
        return listOf(
                TypeCodeGenerator.SizePart.Constant(if (isBoxed) CHECK_FOR_NULL_SIZE else 0),
                TypeCodeGenerator.SizePart.Constant(type.getPrimitiveSize())
        )
    }
}