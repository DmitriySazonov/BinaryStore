package com.example.binaryjson.generator.adapter

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import java.util.*
import javax.lang.model.element.Modifier

const val VALUE = "value"
const val META_STORE_FIELD = "metadataStore"
const val VERSION_FIELD = "versionId"
const val ADAPTER_FIELD_SUFFIX = "Adapter"

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