package com.example.binaryjson.generator.adapter

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

object AdapterSerializeBuilder : CodeBuilder {

    private const val SERIALIZE_METHOD = "serialize"
    private const val BUFFER_NAME = "byteBuffer"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addMethods(generateArraysSerializeMethods(context.metadata))
        addMethod(generateSerializeMethod(context.metadata))
    }

    private fun generateSerializeMethod(metadata: TypeMetadata): MethodSpec {
        return adapterMethod(SERIALIZE_METHOD) {
            addParameter(ByteBuffer::class.java, BUFFER_NAME)
            addParameter(TypeName.get(metadata.element.asType()), VALUE)
            addCode(generateSerializeCode(metadata))
            addException(Exception::class.java)
        }
    }

    private fun generateSerializeCode(metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            addStatement("${BUFFER_NAME}.write(${metadata.versionId})")
            metadata.fields.map {
                generateSerializeCode("${VALUE}.${it.name}", it.type)
            }.forEach(::addStatement)
        }.build()
    }

    private fun generateArraysSerializeInvoke(fieldName: String, type: ArrayTypeName): String {
        return "${serializeArrayMethodName(type)}($BUFFER_NAME, $fieldName)"
    }

    private fun generateArraysSerializeMethods(metadata: TypeMetadata): List<MethodSpec> {
        return metadata.fields.filter {
            it.type is ArrayTypeName
        }.map {
            val name = it.name
            val type = it.type as ArrayTypeName
            MethodSpec.methodBuilder(serializeArrayMethodName(type)).apply {
                addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                addException(Exception::class.java)
                addParameter(ByteBuffer::class.java, BUFFER_NAME)
                addParameter(type, VALUE)
                addCode(generateSerializeCodeArray(type))
            }.build()
        }
    }

    private fun generateSerializeCodeArray(type: ArrayTypeName): CodeBlock {
        val baseType = type.baseType
        return CodeBlock.builder().apply {
            forEach(VALUE, type, beforeFor = {
                addStatement("${BUFFER_NAME}.write(${it}.length)")
            }) {
                addStatement(generateSerializeCode(it, baseType))
            }
        }.build()
    }

    private fun generateSerializeCode(fieldName: String, type: TypeName): String {
        return when {
            type.isPrimitive -> generateSerializeCodePrimitive(fieldName)
            type is ClassName -> generateSerializeCodeClass(fieldName, type)
            type is ParameterizedTypeName -> generateSerializeCodeClass(fieldName, type.rawType)
            type is ArrayTypeName -> generateArraysSerializeInvoke(fieldName, type)
            else -> throw IllegalArgumentException("Fail generate serialization code. " +
                    "Unknown type $type")
        }
    }

    private fun generateSerializeCodePrimitive(fieldName: String): String {
        return "${BUFFER_NAME}.write(${fieldName})"
    }

    private fun generateSerializeCodeClass(fieldName: String, className: ClassName): String {
        return adapterFiledName(className) + ".$SERIALIZE_METHOD($BUFFER_NAME, ${fieldName})"
    }

    private fun serializeArrayMethodName(type: ArrayTypeName): String {
        return "${SERIALIZE_METHOD}_${type.simpleName}Array"
    }
}
