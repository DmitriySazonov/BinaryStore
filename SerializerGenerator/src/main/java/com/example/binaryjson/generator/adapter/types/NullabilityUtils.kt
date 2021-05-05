package com.example.binaryjson.generator.adapter.types

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.BufferGeneratorHelper
import com.example.binaryjson.generator.BufferGeneratorHelper.FALSE_CONST
import com.example.binaryjson.generator.BufferGeneratorHelper.TRUE_CONST
import com.example.binaryjson.generator.BufferName
import com.example.binaryjson.generator.ValueName
import com.squareup.javapoet.CodeBlock

const val CHECK_FOR_NULL_SIZE = ByteBuffer.BYTE_BYTES

fun CodeBlock.Builder.checkForNullAndWrite(
        value: ValueName,
        buffer: BufferName,
        code: CodeBlock.Builder.() -> Unit,
) {
    generateIf(
            expression = "${value.name} != null",
            positiveCode = {
                val expression = BufferGeneratorHelper.invoke_write(buffer,
                        ValueName("\$T.$TRUE_CONST"))
                addStatement(expression, BufferGeneratorHelper.type)
                code()
            },
            negativeCode = {
                val expression = BufferGeneratorHelper.invoke_write(buffer,
                        ValueName("\$T.$FALSE_CONST"))
                addStatement(expression, BufferGeneratorHelper.type)
            }
    )
}

fun CodeBlock.Builder.checkForNull(
        valueName: ValueName,
        nonnullCode: (CodeBlock.Builder.() -> Unit)? = null,
        nullCode: (CodeBlock.Builder.() -> Unit)? = null
) {
    generateIf(
            expression = "${valueName.name} != null",
            invertedExpression = "${valueName.name} == null",
            positiveCode = nonnullCode,
            negativeCode = nullCode
    )
}

fun CodeBlock.Builder.checkForNullInBuffer(
        buffer: BufferName,
        nonnullCode: CodeBlock.Builder.() -> Unit,
        nullCode: CodeBlock.Builder.() -> Unit
) {
    val readByte = BufferGeneratorHelper.invoke_readByte()
    generateIf("${buffer.name}.$readByte == ${ByteBuffer.TRUE}",
            positiveCode = nonnullCode, negativeCode = nullCode)
}

fun CodeBlock.Builder.generateIf(
        expression: String,
        invertedExpression: String = "!($expression)",
        positiveCode: (CodeBlock.Builder.() -> Unit)? = null,
        negativeCode: (CodeBlock.Builder.() -> Unit)? = null
) {
    when {
        positiveCode != null && negativeCode != null -> {
            beginControlFlow("if ($expression)")
            positiveCode.invoke(this)
            nextControlFlow("else")
            negativeCode.invoke(this)
            endControlFlow()
        }
        positiveCode != null && negativeCode == null -> {
            beginControlFlow("if ($expression)")
            positiveCode.invoke(this)
            endControlFlow()
        }
        positiveCode == null && negativeCode != null -> {
            beginControlFlow("if ($invertedExpression)")
            negativeCode.invoke(this)
            endControlFlow()
        }
    }
}