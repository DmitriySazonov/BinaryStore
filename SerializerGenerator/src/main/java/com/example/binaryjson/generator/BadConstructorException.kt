package com.example.binaryjson.generator

import com.binarystore.InjectType
import com.binarystore.annotation.BinaryConstructor
import com.squareup.javapoet.TypeName

sealed class BadConstructorException(message: String) : Exception(message) {

    class FullMachError(typeName: TypeName) : BadConstructorException(
            "Doesn't find full match constructor for " +
                    "type ${typeName}. Try to use another ${InjectType::class.java.simpleName} " +
                    "or add appropriate constructor."
    )

    class NotExistAppropriateConstructor(typeName: TypeName) : BadConstructorException(
            "Couldn't find appropriate constructor for " +
                    "type ${typeName}. Check your constructors' signatures."
    )

    class InappropriateForcedConstructor(typeName: TypeName) : BadConstructorException(
            "Class $typeName has inappropriate forced constructor. " +
                    "Check that all of its parameters are the fields."
    )

    class MultiForcedConstructor(typeName: TypeName, count: Int) : BadConstructorException(
            "Class $typeName has $count forced constructor. " +
                    "Class should contains only one such constructor." +
                    "Remove unwanted annotations ${BinaryConstructor::class.java.simpleName}"
    )
}