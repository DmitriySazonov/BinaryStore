@file:Suppress("FunctionName")

package com.example.binaryjson.generator

import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.adapter.Key
import com.binarystore.buffer.ByteBuffer
import com.binarystore.dependency.MultiProperties
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.*

object KeyGeneratorHelper {

    val clazz = Key::class.java
    val type = ClassName.get(clazz)

    fun invoke_saveTo(bufferName: BufferName): String = "saveTo(${bufferName.name})"
    fun invoke_read(bufferName: BufferName): String = "read(${bufferName.name})"
    fun invoke_getSize(): String = "getSize()"
}

object BinaryAdapterGeneratorHelper {

    val clazz = BinaryAdapter::class.java
    val type = ClassName.get(clazz)

    fun invoke_key(): String = "key()"

    fun invoke_deserialize(bufferName: BufferName): String = "deserialize(${bufferName.name})"
    fun invoke_serialize(valueName: ValueName, bufferName: BufferName): String =
            "serialize(${bufferName.name}, ${valueName.name})"

    fun invoke_getSize(valueName: ValueName): String = "getSize(${valueName.name})"
}

object AdapterProviderGeneratorHelper {

    val clazz = BinaryAdapterProvider::class.java
    val type = ClassName.get(clazz)

    fun invoke_getAdapterForClass(
            classExpression: InlineExpression,
            properties: PropertiesName?
    ): String = "getAdapterForClass(${classExpression.expression}, ${properties?.name})"

    fun invoke_getAdapterByKey(
            keyExpression: InlineExpression,
            properties: PropertiesName?
    ): String = "getAdapterByKey(${keyExpression.expression}, ${properties?.name})"
}

object BufferGeneratorHelper {

    val clazz = ByteBuffer::class.java
    val type = ClassName.get(clazz)

    const val TRUE_CONST = "TRUE"
    const val FALSE_CONST = "FALSE"

    fun invoke_write(buffer: BufferName, value: ValueName): String =
            "${buffer.name}.write(${value.name})"

    fun invoke_readByte(): String = "readByte()"

    fun invoke_readByType(bufferName: BufferName, typeName: TypeName): String =
            "${bufferName.name}.read${typeName.toString().capitalize(Locale.ROOT)}()"
}

object MultiPropertiesGeneratorHelper {

    val clazz = MultiProperties::class.java
    val type = ClassName.get(clazz)

    fun invoke_addNewProperty(expression: InlineExpression): String =
            "addProperty(${expression.expression})"
}