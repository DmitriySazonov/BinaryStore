package com.example.binaryjson.generator

import com.binarystore.InjectType
import com.squareup.javapoet.TypeName

class BadConstructorException : Exception {

    constructor(typeName: TypeName) {
        throw IllegalArgumentException("Doesn't find full match constructor for " +
                "type ${typeName}. Try to use another ${InjectType::class.java.simpleName} " +
                "or add appropriate constructor")
    }
}