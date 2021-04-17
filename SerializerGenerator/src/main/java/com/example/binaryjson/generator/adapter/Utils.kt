package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.Key
import com.example.binaryjson.generator.Id
import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.Modifier

const val VALUE = "value"
const val META_STORE_FIELD = "metadataStore"
const val VERSION_FIELD = "versionId"
const val ADAPTER_FIELD_SUFFIX = "Adapter"

fun Id.generateReturnCode(spec: MethodSpec.Builder) {
    when (this) {
        is Id.Int -> {
            spec.addStatement("return new \$T(${value})", Key.Int::class.java)
            spec.returns(Key.Int::class.java)
        }
        is Id.String -> {
            spec.addStatement("return new \$T(\"${value}\")", Key.String::class.java)
            spec.returns(Key.String::class.java)
        }
    }
}

inline fun CodeBlock.Builder.forEach(
        name: String,
        array: ArrayTypeName,
        beforeFor: (itemAccess: String) -> Unit = {},
        code: (itemAccess: String) -> Unit
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

fun adapterFiledName(type: ClassName): String {
    return "${type.simpleName()}${ADAPTER_FIELD_SUFFIX}"
            .decapitalize(Locale.getDefault())
}

fun adapterMethod(name: String, builder: MethodSpec.Builder.() -> Unit): MethodSpec {
    return MethodSpec.methodBuilder(name).apply {
        addAnnotation(Override::class.java)
        addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        builder()
    }.build()
}

val ArrayTypeName.baseType: TypeName
    get() {
        var type = componentType
        while (type is ArrayTypeName) {
            type = type.componentType
        }
        return type
    }

val ArrayTypeName.deep: Int
    get() {
        var type = componentType
        var deep = 1
        while (type is ArrayTypeName) {
            type = type.componentType
            deep++
        }
        return deep
    }

val TypeName.simpleName: String
    get() = when (this) {
        is ParameterizedTypeName -> rawType.simpleName()
        is ClassName -> simpleName()
        is ArrayTypeName -> baseType.simpleName
        else -> toString()
    }
