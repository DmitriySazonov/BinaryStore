package com.example.binaryjson.generator.adapter

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.FieldMetadata
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

object AdapterGetSizeBuilder {

    private const val GET_SIZE_METHOD = "getSize"

    fun generateSizeMethod(metadata: TypeMetadata): MethodSpec {
        return MethodSpec.methodBuilder(GET_SIZE_METHOD)
                .addAnnotation(Override::class.java)
                .addException(Exception::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addParameter(TypeName.get(metadata.element.asType()), VALUE)
                .addCode(generateSizeCode(metadata.fields))
                .returns(TypeName.INT)
                .build()
    }

    private fun generateSizeCode(fields: List<FieldMetadata>): CodeBlock {
        return CodeBlock.builder().apply {
            add("return ")
            val primitiveSplit = fields.groupBy { it.type.isPrimitive }
            primitiveSplit[false]?.forEach {
                val code = when (val type = it.type) {
                    is ClassName -> generateSizeCodeClass(it.name, type)
                    is ParameterizedTypeName -> generateSizeCodeClass(it.name, type.rawType)
                    else -> throw IllegalArgumentException("Fail generate getSize code. " +
                            "Unknown type $type")
                }
                add("$code + \n")
            }
            val primitiveSum = (primitiveSplit[true]?.sumBy { getPrimitiveSize(it.type) } ?: 0) +
                    AdapterDeserializeBuilder.VERSION_TAG_SIZE
            add("$primitiveSum;\n")
        }.build()
    }

    private fun getPrimitiveSize(type: TypeName): Int {
        return when (type.toString()) {
            "boolean" -> ByteBuffer.BOOLEAN_BYTES
            "byte" -> ByteBuffer.BYTE_BYTES
            "short" -> ByteBuffer.SHORT_BYTES
            "int" -> ByteBuffer.INTEGER_BYTES
            "long" -> ByteBuffer.LONG_BYTES
            "float" -> ByteBuffer.FLOAT_BYTES
            "double" -> ByteBuffer.DOUBLE_BYTES
            else -> throw IllegalArgumentException("unknown primitive type $type")
        }
    }

    private fun generateSizeCodeClass(fieldName: String, className: ClassName): String {
        return adapterFiledName(className) +
                ".${GET_SIZE_METHOD}(${VALUE}.${fieldName})"
    }
}