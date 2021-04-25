package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.adapter.getPrimitiveSize
import com.example.binaryjson.generator.simpleName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.TypeName
import java.util.*

class PrimitiveCodeEntry(
        private val type: TypeName,
) : AdapterCodeEntry {

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.addStatement("${bufferName}.write(${valueName})")
    }

    override fun generateDeserialize(
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): AdapterCodeEntry.ValueName {
        val valueName = context.generateValName()
        val deserializeCode = "${bufferName}.read${type.toString().capitalize(Locale.ROOT)}()"
        builder.addStatement("final $type $valueName = $deserializeCode")
        return AdapterCodeEntry.ValueName(valueName)
    }

    override fun generateGetSize(
            valueName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): List<AdapterCodeEntry.SizePart> {
        return listOf(
                AdapterCodeEntry.SizePart.Constant(type.getPrimitiveSize())
        )
    }
}