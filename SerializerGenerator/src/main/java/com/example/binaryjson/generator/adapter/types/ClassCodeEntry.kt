package com.example.binaryjson.generator.adapter.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock

class ClassCodeEntry(
        private val className: ClassName,
) : AdapterCodeEntry {

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.addStatement("${context.adapterName(className)}.serialize($bufferName, $valueName)")
    }

    override fun generateDeserialize(
            bufferName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): AdapterCodeEntry.ValueName {
        val valueName = context.generateValName()
        val deserializeCode = "${context.adapterName(className)}.deserialize($bufferName)"
        builder.addStatement("final \$T $valueName = $deserializeCode", className)
        return AdapterCodeEntry.ValueName(valueName)
    }

    override fun generateGetSize(
            valueName: String,
            context: AdapterCodeEntry.Context,
            builder: CodeBlock.Builder,
    ): List<AdapterCodeEntry.SizePart> {
        return listOf(
                AdapterCodeEntry.SizePart.Expression(
                        "${context.adapterName(className)}.getSize($valueName)"
                )
        )
    }
}