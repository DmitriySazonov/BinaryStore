package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.BinaryAdapterGeneratorHelper
import com.example.binaryjson.generator.BinaryAdapterGeneratorHelper.invoke_key
import com.example.binaryjson.generator.KeyGeneratorHelper
import com.example.binaryjson.generator.KeyGeneratorHelper.invoke_getSize
import com.example.binaryjson.generator.KeyGeneratorHelper.invoke_saveTo
import com.example.binaryjson.generator.TypeMeta
import com.squareup.javapoet.CodeBlock

class ClassCodeGenerator(
        private val metaType: TypeMeta.Class,
) : TypeCodeGenerator {

    override fun generateSerialize(
            valueName: String,
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByClass(context, valueName, builder)
        }
        if (!metaType.staticType) {
            builder.addStatement("${adapterName}.${invoke_key()}.${invoke_saveTo(bufferName)}")
        }
        builder.addStatement("${adapterName}.serialize($bufferName, $valueName)")
    }

    override fun generateDeserialize(
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.ValueName {
        val valueName = context.generateValName()
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            val keyName = context.generateValName()
            val keyType = KeyGeneratorHelper.type
            val invokeRead = KeyGeneratorHelper.invoke_read(bufferName)
            builder.addStatement("final \$T $keyName = \$T.$invokeRead",
                    keyType, keyType)
            context.generateAdapterByKeyExpression(keyExpression = keyName)
        }
        val deserializeCode = "${adapterName}.${
            BinaryAdapterGeneratorHelper
                    .invoke_deserialize(bufferName)
        }"
        if (metaType.staticType) {
            builder.addStatement("final \$T $valueName = $deserializeCode", metaType.type)
        } else {
            builder.addStatement("final \$T $valueName = (\$T) $deserializeCode", metaType.type,
                    metaType.type)
        }
        return TypeCodeGenerator.ValueName(valueName)
    }

    override fun generateGetSize(
            valueName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<TypeCodeGenerator.SizePart> {
        val sizeParts = ArrayList<TypeCodeGenerator.SizePart>()
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByClass(context, valueName, builder)
        }

        val getSizeExpression = "${adapterName}.${
            BinaryAdapterGeneratorHelper
                    .invoke_getSize(valueName)
        }"
        if (!metaType.staticType) {
            val keySizeExpression = "${adapterName}.${invoke_key()}.${invoke_getSize()}"
            sizeParts += TypeCodeGenerator.SizePart.Expression(keySizeExpression)
        }
        sizeParts += TypeCodeGenerator.SizePart.Expression(getSizeExpression)
        return sizeParts
    }

    private fun generateAdapterByClass(
            context: TypeCodeGenerator.Context,
            valueName: String,
            builder: CodeBlock.Builder,
    ): String {
        val expression = context.generateAdapterForClassExpression(
                classExpression = "(Class<\$T>) ${valueName}.getClass()"
        )
        val name = context.generateValName()
        builder.addStatement("final \$T $name = $expression",
                context.getAdapterTypeNameFor(metaType.type), metaType.type)
        return name
    }
}