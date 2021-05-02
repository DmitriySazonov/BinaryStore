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
        builder.checkForNullAndWrite(valueName, bufferName) {
            val adapterName = if (metaType.staticType) {
                context.getOrCreateAdapterFieldFor(metaType.type)
            } else {
                generateAdapterByClass(context, valueName, builder)
            }
            if (!metaType.staticType) {
                addStatement("${adapterName}.${invoke_key()}.${invoke_saveTo(bufferName)}")
            }
            addStatement("${adapterName}.${
                BinaryAdapterGeneratorHelper
                        .invoke_serialize(valueName, bufferName)
            }")
        }
    }

    override fun generateDeserialize(
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.ValueName {
        val valueName = context.generateValName()
        builder.addStatement("final \$T $valueName", metaType.type)
        builder.checkForNullInBuffer(bufferName, nonnullCode = {
            generateNonNullBranchDeserialize(valueName, bufferName, context, builder)
        }, nullCode = {
            addStatement("$valueName = null")
        })
        return TypeCodeGenerator.ValueName(valueName)
    }

    override fun generateGetSize(
            valueName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<TypeCodeGenerator.SizePart> {
        val accumulatorName = context.generateValName()
        builder.addStatement("int $accumulatorName = 0")
        builder.checkForNull(valueName, nonnullCode = {
            generateNonnullBranchGetSize(valueName, accumulatorName, context, builder)
        })
        return listOf(
                TypeCodeGenerator.SizePart.Constant(CHECK_FOR_NULL_SIZE),
                TypeCodeGenerator.SizePart.Expression(accumulatorName)
        )
    }

    private fun generateNonnullBranchGetSize(
            valueName: String,
            accumulatorName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ) {
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByClass(context, valueName, builder)
        }

        val getSizeExpression = "${adapterName}." + BinaryAdapterGeneratorHelper
                .invoke_getSize(valueName)
        if (!metaType.staticType) {
            val keySizeExpression = "${adapterName}.${invoke_key()}.${invoke_getSize()}"
            builder.addStatement("$accumulatorName += $keySizeExpression")
        }
        builder.addStatement("$accumulatorName += $getSizeExpression")
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

    private fun generateNonNullBranchDeserialize(
            valueName: String,
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ) {
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByKeyFromBuffer(bufferName, context, builder)
        }
        val deserializeCode = "$adapterName." + BinaryAdapterGeneratorHelper
                .invoke_deserialize(bufferName)
        if (metaType.staticType) {
            builder.addStatement("$valueName = $deserializeCode")
        } else {
            builder.addStatement("$valueName = (\$T) $deserializeCode", metaType.type)
        }
    }

    private fun generateAdapterByKeyFromBuffer(
            bufferName: String,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ): String {
        val keyName = context.generateValName()
        val keyType = KeyGeneratorHelper.type
        val invokeRead = KeyGeneratorHelper.invoke_read(bufferName)
        builder.addStatement("final \$T $keyName = \$T.$invokeRead",
                keyType, keyType)
        return context.generateAdapterByKeyExpression(keyExpression = keyName)
    }
}