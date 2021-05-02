package com.example.binaryjson.generator.adapter.types

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.BufferGeneratorHelper
import com.example.binaryjson.generator.BufferGeneratorHelper.FALSE_CONST
import com.example.binaryjson.generator.BufferGeneratorHelper.TRUE_CONST
import com.squareup.javapoet.CodeBlock

const val CHECK_FOR_NULL_SIZE = ByteBuffer.BYTE_BYTES

fun CodeBlock.Builder.checkForNullAndWrite(
        valueName: String,
        bufferName: String,
        code: CodeBlock.Builder.() -> Unit,
) {
    beginControlFlow("if ($valueName != null)")
    addStatement("${bufferName}.write(\$T.$TRUE_CONST)", BufferGeneratorHelper.type)

    code()

    nextControlFlow("else")
    addStatement("${bufferName}.write(\$T.$FALSE_CONST)", BufferGeneratorHelper.type)
    endControlFlow()
}

fun CodeBlock.Builder.checkForNull(
        valueName: String,
        nonnullCode: (CodeBlock.Builder.() -> Unit)? = null,
        nullCode: (CodeBlock.Builder.() -> Unit)? = null
) {
    beginControlFlow("if (${valueName} != null)")
    nonnullCode?.invoke(this)
    nextControlFlow("else")
    nullCode?.invoke(this)
    endControlFlow()
}

fun CodeBlock.Builder.checkForNullInBuffer(
        bufferName: String,
        nonnullCode: CodeBlock.Builder.() -> Unit,
        nullCode: CodeBlock.Builder.() -> Unit
) {
    val readByte = BufferGeneratorHelper.invoke_readByte()
    beginControlFlow("if (${bufferName}.$readByte == ${ByteBuffer.TRUE})")
    nonnullCode()
    nextControlFlow("else")
    nullCode()
    endControlFlow()
}