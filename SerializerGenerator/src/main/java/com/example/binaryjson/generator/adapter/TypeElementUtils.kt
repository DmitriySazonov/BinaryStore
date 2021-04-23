package com.example.binaryjson.generator.adapter

import com.binarystore.adapter.BinaryAdapter
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

fun getPrefixAndPackage(element: TypeElement): Pair<String, String> {
    var enclosingElement = element.enclosingElement
    var kind = enclosingElement.kind
    var prefix = ""
    while (kind != ElementKind.PACKAGE) {
        prefix += enclosingElement.simpleName
        enclosingElement = enclosingElement.enclosingElement
        kind = enclosingElement.kind
    }
    return prefix to enclosingElement.toString()
}

fun getAdapterInterfaceType(element: TypeElement): TypeName {
    val typeName = TypeName.get(element.asType())
    val className = ClassName.get(BinaryAdapter::class.java)
    return ParameterizedTypeName.get(className, typeName)
}