package com.example.binaryjson.generator.adapter

import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.FieldMeta
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

object AdapterGetSizeBuilder : CodeBuilder {

    private const val GET_SIZE_METHOD = "getSize"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addMethods(generateArraysGetSizeMethods(context.metadata))
        addMethod(generateSizeMethod(context.metadata))
    }

    private fun generateSizeMethod(metadata: TypeMetadata): MethodSpec {
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

    private fun generateSizeCode(fields: List<FieldMeta>): CodeBlock {
        return CodeBlock.builder().apply {
            add("return ")
            val primitiveSplit = fields.groupBy { it.type.isPrimitive }
            primitiveSplit[false]?.forEach {
                val code = generateGetFieldSizeCode("${VALUE}.${it.name}", it.type)
                add("$code + \n")
            }
            val primitiveSum = (primitiveSplit[true]?.sumBy { getPrimitiveSize(it.type) } ?: 0) +
                    AdapterDeserializeBuilder.VERSION_TAG_SIZE
            add("$primitiveSum;\n")
        }.build()
    }

    private fun generateArraysGetSizeMethods(metadata: TypeMetadata): List<MethodSpec> {
        return metadata.fields.mapNotNull {
            it as? FieldMeta.Array
        }.map {
            MethodSpec.methodBuilder(getSizeArrayMethodName(it.type)).apply {
                addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                addException(Exception::class.java)
                addParameter(it.type, VALUE)
                if (it.even && it.baseType.isPrimitive) {
                    addCode(generateGetSizeCodePrimitiveEvenArray(it.type))
                } else {
                    addCode(generateGetSizeCodeArray(it.type))
                }
                returns(TypeName.INT)
            }.build()
        }
    }

    private fun generateGetSizeCodeArray(type: ArrayTypeName): CodeBlock {
        val baseType = type.baseType
        val accumulator = "accumulator"
        return CodeBlock.builder().apply {
            addStatement("int $accumulator = 0")
            forEach(VALUE, type) {
                addStatement("$accumulator += ${generateGetFieldSizeCode(it, baseType)}")
            }
            addStatement("return $accumulator")
        }.build()
    }

    private fun generateGetSizeCodePrimitiveEvenArray(type: ArrayTypeName): CodeBlock {
        val baseType = type.baseType
        return CodeBlock.builder().apply {
            val size = getPrimitiveSize(baseType)
            val arrayDimension = { deep: Int ->
                (0 until deep).joinToString("") { "[0]" }
            }
            add("return $size")
            repeat(type.deep) {
                add(" * $VALUE${arrayDimension(it)}.length")
            }
            add(";")
        }.build()
    }

    private fun generateArraysGetSizeInvoke(expression: String, type: ArrayTypeName): String {
        return "${getSizeArrayMethodName(type)}($expression)"
    }

    private fun getSizeArrayMethodName(type: ArrayTypeName): String {
        return "${GET_SIZE_METHOD}_${type.simpleName}Array"
    }

    private fun generateGetFieldSizeCode(expression: String, type: TypeName): String {
        return when (type) {
            is ClassName -> generateSizeCodeClass(expression, type)
            is ParameterizedTypeName -> generateSizeCodeClass(expression, type.rawType)
            is ArrayTypeName -> generateArraysGetSizeInvoke(expression, type)
            else -> getPrimitiveSize(type).toString()
        }
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
                ".${GET_SIZE_METHOD}($fieldName)"
    }
}
