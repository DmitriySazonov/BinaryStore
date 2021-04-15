package com.example.binaryjson.generator.adapter

import com.binarystore.InjectType
import com.binarystore.buffer.ByteBuffer
import com.example.binaryjson.generator.TypeMetadata
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.Modifier

object AdapterDeserializeBuilder : CodeBuilder {

    const val VERSION_TAG_SIZE = ByteBuffer.INTEGER_BYTES
    private const val DESERIALIZE_METHOD = "deserialize"
    private const val BUFFER_NAME = "byteBuffer"
    private const val VERSION_NAME = "version"

    override fun TypeSpec.Builder.build(context: CodeBuilder.Context) {
        addMethods(generateArraysDeserializeMethods(context.metadata))
        addMethod(generateDeserializeMethod(context.metadata))
    }

    private fun generateDeserializeMethod(metadata: TypeMetadata): MethodSpec {
        return MethodSpec.methodBuilder(DESERIALIZE_METHOD).apply {
            addAnnotation(Override::class.java)
            addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            addException(Exception::class.java)
            addParameter(ByteBuffer::class.java, BUFFER_NAME)
            addCode(generateDeserializeCode(metadata))
            returns(TypeName.get(metadata.element.asType()))
        }.build()
    }

    private fun generateDeserializeCode(metadata: TypeMetadata): CodeBlock {
        return when (metadata.injectType) {
            InjectType.ASSIGNMENT -> generateDeserializeAssignmentCode(metadata)
            InjectType.CONSTRUCTOR -> generateDeserializeConstructorCode(metadata)
        }
    }

    private fun generateDeserializeAssignmentCode(metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            addStatement(readVersionCode())
            addStatement("\$T $VALUE = new \$T()", metadata.element, metadata.element)
            metadata.fields.forEach {
                addStatement("${VALUE}.${it.name} = ${deserializeCode(it.type)}")
            }
            add("return $VALUE;")
        }.build()
    }

    private fun generateDeserializeConstructorCode(metadata: TypeMetadata): CodeBlock {
        return CodeBlock.builder().apply {
            addStatement(readVersionCode())
            add("return new \$T(\n", metadata.element)
            metadata.fields.map {
                CodeBlock.of(deserializeCode(it.type))
            }.also {
                add(CodeBlock.join(it, ",\n"))
            }
            add("\n);\n")
        }.build()
    }

    private fun readVersionCode(): String {
        return "int $VERSION_NAME = ${BUFFER_NAME}.readInt()"
    }

    private fun deserializeCode(type: TypeName): String {
        return when {
            type is ClassName -> deserializeCodeClass(type)
            type is ParameterizedTypeName -> deserializeCodeClass(type.rawType)
            type is ArrayTypeName -> generateArraysDeserializeInvoke(type)
            type.isPrimitive -> deserializeCodePrimitive(type)
            else -> throw IllegalArgumentException("Fail generate deserialization code. " +
                    "Unknown $type")
        }
    }

    private fun generateArraysDeserializeMethods(metadata: TypeMetadata): List<MethodSpec> {
        return metadata.fields.filter {
            it.type is ArrayTypeName
        }.map {
            val type = it.type as ArrayTypeName
            MethodSpec.methodBuilder(getDeserializeMethodName(type)).apply {
                addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                addException(Exception::class.java)
                addParameter(ByteBuffer::class.java, BUFFER_NAME)
                addCode(generateDeserializeCodeArray(type))
                returns(type)
            }.build()
        }
    }

    private fun generateDeserializeCodeArray(type: ArrayTypeName): CodeBlock {
        val baseType = type.baseType
        var deep = type.deep
        val array = "array"
        return CodeBlock.builder().apply {
            val arrayDefine = (0 until deep).joinToString("") { "[]" }
            val arrayDimension = { deep: Int ->
                (0 until deep).joinToString("") {
                    if (it == 0) "[${BUFFER_NAME}.readInt()]" else "[]"
                }
            }
            addStatement("\$T$arrayDefine $array", baseType)
            forEach(array, type, beforeFor = {
                addStatement("$it = new \$T${arrayDimension(deep--)}", baseType)
            }) {
                addStatement("$it = ${deserializeCode(baseType)}")
            }
            addStatement("return $array")
        }.build()
    }

    private fun generateArraysDeserializeInvoke(type: ArrayTypeName): String {
        return "${getDeserializeMethodName(type)}($BUFFER_NAME)"
    }

    private fun getDeserializeMethodName(type: ArrayTypeName): String {
        return "${DESERIALIZE_METHOD}_${type.simpleName}Array"
    }

    private fun deserializeCodePrimitive(type: TypeName): String {
        return "${BUFFER_NAME}.read${type.toString().capitalize(Locale.ROOT)}()"
    }

    private fun deserializeCodeClass(className: ClassName): String {
        return adapterFiledName(className) + ".$DESERIALIZE_METHOD($BUFFER_NAME)"
    }
}
