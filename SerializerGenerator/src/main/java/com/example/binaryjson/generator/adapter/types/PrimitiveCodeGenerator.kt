package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.TypeMeta
import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.squareup.javapoet.CodeBlock
import java.util.*

class PrimitiveCodeGenerator(
        private val typeMeta: TypeMeta.Primitive,
) : TypeCodeGenerator {

    private val type = typeMeta.type
    private val isBoxed = typeMeta.type.isBoxedPrimitive

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        if (isBoxed) {
            builder.checkForNullAndWrite(valueName, bufferName) {
                builder.addStatement("${bufferName}.write(${valueName})")
            }
        } else {
            builder.addStatement("${bufferName}.write(${valueName})")
        }
    }

    override fun generateDeserialize(
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.ValueName {
        val valueName = context.generateValName()
        val deserializeCode = "${bufferName}.read${type.toString().capitalize(Locale.ROOT)}()"
        builder.addStatement("final $type $valueName = $deserializeCode")
        return TypeCodeGenerator.ValueName(valueName)
    }

    override fun generateGetSize(
            valueName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<TypeCodeGenerator.SizePart> {
        return listOf(
                TypeCodeGenerator.SizePart.Constant(type.getPrimitiveSize())
        )
    }
}