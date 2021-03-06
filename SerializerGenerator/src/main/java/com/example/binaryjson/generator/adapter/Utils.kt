package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.Key
import com.example.binaryjson.generator.Id
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

val Id.keyClass
    get() = when (this) {
        is Id.Int -> Key.Int::class.java
        is Id.String -> Key.String::class.java
    }

fun Id.generateStaticFiled(name: String, builder: TypeSpec.Builder) {
    val clazz = keyClass
    val initializer = when (this) {
        is Id.Int -> "new \$T(${value})"
        is Id.String -> "new \$T(\"${value}\")"
    }
    builder.addField(FieldSpec.builder(clazz, name).apply {
        addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        initializer(initializer, clazz)
    }.build())
}

inline fun CodeBlock.Builder.forEach(
        name: String,
        array: ArrayTypeName,
        beforeFor: (itemAccess: String) -> Unit = {},
        code: (itemAccess: String) -> Unit,
) {
    val itemAccess = { deep: Int ->
        name + (0 until deep).joinToString("") { "[i$it]" }
    }
    val deep = array.deep
    repeat(deep) {
        beforeFor(itemAccess(it))
        beginControlFlow("for (int i$it = 0; i$it < ${itemAccess(it)}.length; i$it ++)")
    }
    code(itemAccess(deep))
    repeat(deep) {
        endControlFlow()
    }
}
