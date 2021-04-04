package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.ByteBuffer
import com.example.binaryjson.generator.FieldMetadata
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*

object AdapterSerializeBuilder {

    private const val SERIALIZE_METHOD = "serialize"
    private const val BUFFER_NAME = "byteBuffer"

    fun generateSerializeMethod(metadata: TypeMetadata): MethodSpec {
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
                val type = it.type
                when {
                    type.isPrimitive -> generateSerializeCodePrimitive(it)
                    type is ClassName -> generateSerializeCodeClass(it.name, type)
                    type is ParameterizedTypeName -> generateSerializeCodeClass(it.name, type.rawType)
                    else -> throw IllegalArgumentException("Fail generate serialization code. " +
                            "Unknown type ${it.type}")
                }
            }.forEach(::addStatement)
        }.build()
    }

    private fun generateSerializeCodePrimitive(field: FieldMetadata): String {
        return "${BUFFER_NAME}.write(${VALUE}.${field.name})"
    }

    private fun generateSerializeCodeClass(fieldName: String, className: ClassName): String {
        return adapterFiledName(className) +
                ".$SERIALIZE_METHOD($BUFFER_NAME, ${VALUE}.${fieldName})"
    }
}