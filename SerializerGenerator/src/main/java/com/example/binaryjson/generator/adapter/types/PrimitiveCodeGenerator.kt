package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.example.binaryjson.generator.adapter.types.TypeCodeGenerator.*
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
            context: Context,
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
            variable: Variable,
            properties: PropertiesName?,
            context: Context,
            builder: CodeBlock.Builder,
    ): DeserializeResult {
        val valueName = variable.name
        val primitiveType = type.unbox()
        val deserializeCode = BufferGeneratorHelper.invoke_readByType(buffer, primitiveType)
        if (isBoxed) {
            if (variable is Variable.Unexcited) {
                builder.addStatement("final \$T $valueName", type)
            }
            builder.checkForNullInBuffer(buffer, nonnullCode = {
                addStatement("$valueName = $deserializeCode")
            }, nullCode = {
                addStatement("$valueName = null")
            })
        } else {
            val def = if (variable is Variable.Unexcited) "final $type " else ""
            builder.addStatement("$def$valueName = $deserializeCode")
        }
        return DeserializeResult(valueName)
    }

    override fun generateGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: Context,
            builder: CodeBlock.Builder,
    ): List<SizePart> {
        return listOf(
                SizePart.Constant(if (isBoxed) CHECK_FOR_NULL_SIZE else 0),
                SizePart.Constant(type.getPrimitiveSize())
        )
    }
}