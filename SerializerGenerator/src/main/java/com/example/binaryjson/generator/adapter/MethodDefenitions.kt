package com.example.binaryjson.generator.adapter

import com.binarystore.buffer.ByteBuffer
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

private const val GET_SIZE_METHOD = "getSize"
private const val SERIALIZE_METHOD = "serialize"
private const val DESERIALIZE_METHOD = "deserialize"

fun generateSerializeMethod(
        valueName: String,
        valueType: TypeName,
        bufferName: String,
        codeBlock: CodeBlock,
): MethodSpec {
    return adapterMethod(SERIALIZE_METHOD) {
        addParameter(ByteBuffer::class.java, bufferName)
        addParameter(valueType, valueName)
        addCode(codeBlock)
        addException(Exception::class.java)
    }
}

fun generateDeserializeMethod(
        valueType: TypeName,
        bufferName: String,
        codeBlock: CodeBlock,
): MethodSpec {
    return MethodSpec.methodBuilder(DESERIALIZE_METHOD).apply {
        addAnnotation(Override::class.java)
        addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        addException(Exception::class.java)
        addParameter(ByteBuffer::class.java, bufferName)
        addCode(codeBlock)
        returns(valueType)
    }.build()
}

fun generateSizeMethod(
        valueName: String,
        valueType: TypeName,
        codeBlock: CodeBlock,
): MethodSpec {
    return MethodSpec.methodBuilder(GET_SIZE_METHOD)
            .addAnnotation(Override::class.java)
            .addException(Exception::class.java)
            .addModifiers(Modifier.PUBLIC)
            .addModifiers(Modifier.FINAL)
            .addParameter(valueType, valueName)
            .addCode(codeBlock)
            .returns(TypeName.INT)
            .build()
}