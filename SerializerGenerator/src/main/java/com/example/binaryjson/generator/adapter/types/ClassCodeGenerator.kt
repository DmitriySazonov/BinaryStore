package com.example.binaryjson.generator.adapter.types

import com.binarystore.adapter.Key
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
        val classExpression = "${value.name}.getClass()"

        val name = context.generateValName()
        val adapterTypeName = context.getAdapterTypeNameFor(metaType.type)
        if (context is ArrayContext) {
            val lastClass = context.generateValName()
            val adapterExpression = context.generateAdapterForClassExpression(
                    classExpression = InlineExpression(lastClass),
                    properties = properties
            )
            context.outerBuilder.addStatement("\$T $name = null", adapterTypeName)
            context.outerBuilder.addStatement("\$T<\$T> $lastClass = null", Class::class.java, metaType.type)
            builder.generateIf("$lastClass != $classExpression", positiveCode = {
                addStatement("$lastClass = (Class<\$T>) $classExpression", metaType.type)
                addStatement("$name = $adapterExpression", metaType.type)
            })
        } else {
            val expression = context.generateAdapterForClassExpression(
                    classExpression = InlineExpression("(Class<\$T>) $classExpression"),
                    properties = properties
            )
            builder.addStatement("final \$T $name = $expression", adapterTypeName, metaType.type)
        }
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

        return if (context is ArrayContext) {
            val adapterTypeName = context.getAdapterTypeNameFor(metaType.type)
            val lastKeyName = context.generateValName()
            val adapterName = context.generateValName()
            val adapterByKeyExpression = context.generateAdapterByKeyExpression(
                    keyExpression = InlineExpression(lastKeyName),
                    properties = properties
            )
            context.outerBuilder.addStatement("\$T $lastKeyName = null", Key::class.java)
            context.outerBuilder.addStatement("\$T $adapterName = null", adapterTypeName)
            builder.generateIf("!$keyName.equals($lastKeyName)", positiveCode = {
                addStatement("$lastKeyName = $keyName")
                addStatement("$adapterName = (\$T) $adapterByKeyExpression", adapterTypeName)
            })
            adapterName
        } else {
            context.generateAdapterByKeyExpression(
                    keyExpression = InlineExpression(keyName),
                    properties = properties
            )
        }
    }
}