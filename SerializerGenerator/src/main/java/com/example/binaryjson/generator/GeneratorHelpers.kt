@file:Suppress("FunctionName")

package com.example.binaryjson.generator

import com.binarystore.adapter.BinaryAdapter
import com.binarystore.adapter.BinaryAdapterProvider
import com.binarystore.adapter.Key
import com.binarystore.buffer.ByteBuffer
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.util.*

object KeyGeneratorHelper {

    val clazz = Key::class.java
    val type = ClassName.get(clazz)

    fun invoke_saveTo(bufferName: String): String = "saveTo($bufferName)"
    fun invoke_read(bufferName: String): String = "read($bufferName)"
    fun invoke_getSize(): String = "getSize()"
}

object BinaryAdapterGeneratorHelper {

    val clazz = BinaryAdapter::class.java
    val type = ClassName.get(clazz)

    fun invoke_key(): String = "key()"

    fun invoke_deserialize(bufferName: String): String = "deserialize($bufferName)"
    fun invoke_serialize(valueName: String, bufferName: String): String =
            "serialize($bufferName, $valueName)"
    fun invoke_getSize(valueName: String): String = "getSize($valueName)"
}

object AdapterProviderGeneratorHelper {

    val clazz = BinaryAdapterProvider::class.java
    val type = ClassName.get(clazz)

    fun invoke_getAdapterForClass(classExpression: String): String =
            "getAdapterForClass($classExpression)"

    fun invoke_getAdapterByKey(keyExpression: String): String =
            "getAdapterByKey($keyExpression)"
}

object BufferGeneratorHelper {

    val clazz = ByteBuffer::class.java
    val type = ClassName.get(clazz)

    fun invoke_readByte(): String = "readByte()"

    fun invoke_readByType(typeName: TypeName): String =
            "read${typeName.toString().capitalize(Locale.ROOT)}()"
}