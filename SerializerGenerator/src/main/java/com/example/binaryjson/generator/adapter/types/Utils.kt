package com.example.binaryjson.generator.adapter.types

import com.example.binaryjson.generator.BinaryAdapterGeneratorHelper
import com.example.binaryjson.generator.KeyGeneratorHelper
import com.squareup.javapoet.CodeBlock

fun CodeBlock.Builder.checkForNullAndWrite(
        valueName: String,
        bufferName: String,
        code: CodeBlock.Builder.() -> Unit,
) {
    beginControlFlow("if ($valueName != null)")
    addStatement("${bufferName}.write($TRUE)")

    code()

    nextControlFlow("else")
    addStatement("${bufferName}.write($FALSE)")
    endControlFlow()
}