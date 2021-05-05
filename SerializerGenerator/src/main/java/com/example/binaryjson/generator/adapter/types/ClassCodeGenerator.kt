package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.*
import com.example.binaryjson.generator.BinaryAdapterGeneratorHelper.invoke_key
import com.example.binaryjson.generator.KeyGeneratorHelper.invoke_getSize
import com.example.binaryjson.generator.KeyGeneratorHelper.invoke_saveTo
import com.squareup.javapoet.CodeBlock

class ClassCodeGenerator(
        private val metaType: TypeMeta.Class,
) : TypeCodeGenerator {

    override fun generateSerialize(
            value: ValueName,
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ) {
        builder.checkForNullAndWrite(value, buffer) {
            val adapterName = if (metaType.staticType) {
                context.getOrCreateAdapterFieldFor(metaType.type)
            } else {
                generateAdapterByClass(value, properties, context, builder)
            }
            if (!metaType.staticType) {
                addStatement("${adapterName}.${invoke_key()}.${invoke_saveTo(buffer)}")
            }
            addStatement("${adapterName}.${
                BinaryAdapterGeneratorHelper
                        .invoke_serialize(value, buffer)
            }")
        }
    }

    override fun generateDeserialize(
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): TypeCodeGenerator.DeserializeResult {
        val valueName = context.generateValName()
        builder.addStatement("final \$T $valueName", metaType.type)
        builder.checkForNullInBuffer(buffer, nonnullCode = {
            generateNonNullBranchDeserialize(valueName, buffer, properties, context, builder)
        }, nullCode = {
            addStatement("$valueName = null")
        })
        return TypeCodeGenerator.DeserializeResult(valueName)
    }

    override fun generateGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): List<TypeCodeGenerator.SizePart> {
        builder.checkForNull(value, nonnullCode = {
            generateNonnullBranchGetSize(value, properties, accumulator, context, builder)
        })
        return listOf(
                TypeCodeGenerator.SizePart.Constant(CHECK_FOR_NULL_SIZE)
        )
    }

    private fun generateNonnullBranchGetSize(
            value: ValueName,
            properties: PropertiesName?,
            accumulator: AccumulatorName,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ) {
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByClass(value, properties, context, builder)
        }

        val getSizeExpression = "${adapterName}." + BinaryAdapterGeneratorHelper
                .invoke_getSize(value)
        if (!metaType.staticType) {
            val keySizeExpression = "${adapterName}.${invoke_key()}.${invoke_getSize()}"
            builder.addStatement("${accumulator.name} += $keySizeExpression")
        }
        builder.addStatement("${accumulator.name} += $getSizeExpression")
    }

    private fun generateAdapterByClass(
            value: ValueName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder,
    ): String {
        val expression = context.generateAdapterForClassExpression(
                classExpression = InlineExpression("(Class<\$T>) ${value.name}.getClass()"),
                properties = properties
        )
        val name = context.generateValName()
        builder.addStatement("final \$T $name = $expression",
                context.getAdapterTypeNameFor(metaType.type), metaType.type)
        return name
    }

    private fun generateNonNullBranchDeserialize(
            value: String,
            buffer: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ) {
        val adapterName = if (metaType.staticType) {
            context.getOrCreateAdapterFieldFor(metaType.type)
        } else {
            generateAdapterByKeyFromBuffer(buffer, properties, context, builder)
        }
        val deserializeCode = "$adapterName." + BinaryAdapterGeneratorHelper
                .invoke_deserialize(buffer)
        if (metaType.staticType) {
            builder.addStatement("$value = $deserializeCode")
        } else {
            builder.addStatement("$value = (\$T) $deserializeCode", metaType.type)
        }
    }

    private fun generateAdapterByKeyFromBuffer(
            bufferName: BufferName,
            properties: PropertiesName?,
            context: TypeCodeGenerator.Context,
            builder: CodeBlock.Builder
    ): String {
        val keyName = context.generateValName()
        val keyType = KeyGeneratorHelper.type
        val invokeRead = KeyGeneratorHelper.invoke_read(bufferName)
        builder.addStatement("final \$T $keyName = \$T.$invokeRead",
                keyType, keyType)
        return context.generateAdapterByKeyExpression(
                keyExpression = InlineExpression(keyName),
                properties = properties
        )
    }
}